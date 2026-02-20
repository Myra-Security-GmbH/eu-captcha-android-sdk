/*!
 * Copyright (c) Myra Security GmbH 2026.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * EU Captcha Android SDK - JavaScript Bridge
 *
 * This bridge runs in the Android WebView and connects the EU Captcha widget
 * (check.all.js) to the Android native layer via the Android JS interface.
 *
 * Architecture:
 * - check.all.js (compiled from check.ts) runs directly in this page (no iframe)
 * - check.ts calls parent.postMessage(data, origin) which equals window.postMessage
 *   since there is no iframe in this context
 * - We override window.postMessage before check.all.js loads to intercept events
 *   without requiring origin matching
 * - Android native calls eucaptchaReset() to reset the widget
 */

// Override window.postMessage to intercept events from check.all.js.
// check.ts calls parent.postMessage(data, targetOrigin). In a non-iframe context,
// parent === window, so this becomes window.postMessage(data, targetOrigin).
// The targetOrigin check would normally block delivery to listeners if origins
// do not match. We bypass this by dispatching the event directly.
(function () {
  window.postMessage = function (data) {
    var evt;
    try {
      evt = new MessageEvent("message", {
        data: data,
        origin: window.location.origin || "null",
        source: window,
      });
    } catch (e) {
      // Fallback for older Android WebView versions
      evt = document.createEvent("MessageEvent");
      evt.initMessageEvent(
        "message",
        false,
        false,
        data,
        window.location.origin || "null",
        "",
        window
      );
    }
    window.dispatchEvent(evt);
  };
})();

function sendToAndroid(msg) {
  try {
    window.Android.receiveMessage(JSON.stringify(msg));
  } catch (e) {
    console.error("EU Captcha: Failed to send message to Android:", e);
  }
}

// Listen for events dispatched by check.all.js via our overridden postMessage
window.addEventListener("message", function (event) {
  var data = event.data;
  if (!data || !data.type) return;

  if (data.type === "euCaptchaCompleted") {
    var response = data.payload || "";
    sendToAndroid({ type: "euc:widget.complete", response: response });
    sendToAndroid({
      type: "euc:widget.statechange",
      state: "completed",
      response: response,
    });
  } else if (data.type === "euCaptchaExpired") {
    sendToAndroid({ type: "euc:widget.expire", response: ".EXPIRED" });
    sendToAndroid({
      type: "euc:widget.statechange",
      state: "expired",
      response: ".EXPIRED",
    });
  } else if (data.type === "euCaptchaError") {
    sendToAndroid({ type: "euc:widget.error", response: ".ERROR" });
    sendToAndroid({
      type: "euc:widget.statechange",
      state: "error",
      response: ".ERROR",
    });
  }
  // euCaptchaDone is an internal event, intentionally ignored
});

// Notify Android that the bridge is ready
if (document.readyState !== "loading") {
  sendToAndroid({ type: "ready" });
} else {
  document.addEventListener("DOMContentLoaded", function () {
    sendToAndroid({ type: "ready" });
  });
}

// Called by Android native to reset the widget (reloads the page)
window.eucaptchaReset = function () {
  window.location.reload();
};
