// ==UserScript==
// @name     NIDAC Authorisation
// @namespace  http://tampermonkey.net/
// @version   0.1
// @description Authenticates for NIDAC
// @author    NIDAC Team
// @match     http*://*/auth-login-stub/gg-sign-in?continue=*national-import-duty-adjustment-centre*
// @grant     none
// @updateURL https://raw.githubusercontent.com/hmrc/national-import-duty-adjustment-centre-frontend/master/docs/NIDAC%20Authorisation.js
// ==/UserScript==

(function () {
    'use strict';
    document.getElementsByName("redirectionUrl")[0].value = getBaseUrl() + "/national-import-duty-adjustment-centre";
    document.getElementById('global-header').appendChild(createQuickButton())
})();

function createQuickButton() {
    let button = document.createElement('button');
    button.id = "quickSubmit";
    button.innerHTML = 'Quick Submit';
    button.onclick = () => document.getElementsByClassName('button')[0].click();
    return button;
}

function getBaseUrl() {
    let host = window.location.host;
    if (window.location.hostname === 'localhost') {
        host = 'localhost:8490'
    }
    return window.location.protocol + "//" + host;
}
