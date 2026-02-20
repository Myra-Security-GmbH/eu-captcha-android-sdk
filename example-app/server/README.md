# Example Server

## Prerequisites
Install [Node.js](https://nodejs.org/).

## Install
```bash
npm install
```

## Run
```bash
EUC_SITEKEY=your_sitekey EUC_SECRET=your_secret npm start
```

Replace `your_sitekey` and `your_secret` with your EU Captcha credentials.

The server verifies captcha responses using the EU Captcha API (`POST https://api.eu-captcha.eu/v1/verify`).
