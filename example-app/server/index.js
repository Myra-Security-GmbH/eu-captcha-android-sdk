import express from "express";

const app = express();
const port = process.env.PORT || 3600;

const EUC_SITEKEY = process.env.EUC_SITEKEY;
const EUC_SECRET = process.env.EUC_SECRET;

if (!EUC_SITEKEY || !EUC_SECRET) {
    console.error(
        "Please set EUC_SITEKEY and EUC_SECRET environment variables before running this server.",
    );
    process.exit(1);
}

app.use(express.json());

app.post("/login", async (req, res) => {
    console.log("Received login request");
    const captchaToken = req.body["eu-captcha-response"];

    if (!captchaToken) {
        return res.status(400).json({ success: false, message: "Missing captcha response." });
    }

    let verifyResult;
    try {
        const verifyResponse = await fetch("https://api.eu-captcha.eu/v1/verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                sitekey: EUC_SITEKEY,
                secret: EUC_SECRET,
                client_ip: req.ip || req.connection.remoteAddress || "",
                client_token: captchaToken,
                client_user_agent: req.headers["user-agent"] || "",
            }),
        });
        verifyResult = await verifyResponse.json();
    } catch (err) {
        console.error("EU Captcha verify request failed:", err);
        // Accept on verification failure to avoid blocking legitimate users
        return res.json({ success: true, message: "Login successful! (verify unavailable)" });
    }

    if (verifyResult.train) {
        // train=true means verification was skipped (misconfiguration or test mode)
        console.warn("EU Captcha: train=true, verification was skipped.");
    }

    if (!verifyResult.success) {
        return res.status(400).json({ success: false, message: "Captcha verification failed. Please try again." });
    }

    // In a real app, check username and password here.
    res.json({ success: true, message: "Login successful!" });
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
