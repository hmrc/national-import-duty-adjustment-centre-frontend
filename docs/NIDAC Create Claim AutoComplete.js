// ==UserScript==
// @name         NIDAC Create Claim AutoComplete
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  NIDAC Create Claim AutoComplete
// @author       You
// @match        http*://*/national-import-duty-adjustment-centre*
// @grant        none
// @updateURL    https://raw.githubusercontent.com/hmrc/national-import-duty-adjustment-centre-frontend/master/docs/NIDAC%20Create%20Claim%20AutoComplete.js
// ==/UserScript==

(function () {
    'use strict';
    document.getElementsByTagName("body")[0].appendChild(createQuickButton());
})();

function createQuickButton() {
    let button = document.createElement('button');
    button.id = "quickSubmit";
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start');
    } else {
        button.classList.add('govuk-button');
    }
    button.style.position = "absolute"
    button.style.top = "50px"
    button.innerHTML = 'Quick Submit';
    button.onclick = () => completePage();
    return button;
}

function currentPageIs(path) {
    let matches = window.location.pathname.match(path + "$");
    return matches && matches.length > 0
}

function submit() {
    document.getElementsByClassName("govuk-button")[0].click();
}

function completePage() {
    if (currentPageIs("/national-import-duty-adjustment-centre/importer-representative")) {
        document.getElementById("representation_type-2").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/claim-type")) {
        document.getElementById("claim_type").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/entry-details")) {
        document.getElementById("entryProcessingUnit").value = "123";
        document.getElementById("entryNumber").value = "123456Q";
        document.getElementById("entryDate").value = "12";
        document.getElementById("entryDate_month").value = "12";
        document.getElementById("entryDate_year").value = "2020";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/item-numbers")) {
        document.getElementById("itemNumbers").value = "1, 2, 7-10";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/reclaiming")) {
        document.getElementById("reclaimDutyType").checked = true;
        document.getElementById("reclaimDutyType-2").checked = true;
        document.getElementById("reclaimDutyType-3").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/enter-customs-duty")) {
        document.getElementById("actuallyPaid").value = "100.00";
        document.getElementById("shouldPaid").value = "89.99";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/enter-import-vat")) {
        document.getElementById("actuallyPaid").value = "80.00";
        document.getElementById("shouldPaid").value = "72.50";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/enter-other-duties")) {
        document.getElementById("actuallyPaid").value = "50.00";
        document.getElementById("shouldPaid").value = "49.99";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/claim-reason")) {
        document.getElementById("claimReason").value = "I believe I have been over-charged";
        submit();
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/upload-supporting-documents")) {
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/uploaded-files")) {
        document.getElementById("yesOrNo-2").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/contact-details")) {
        document.getElementById("firstName").value = "Tim";
        document.getElementById("lastName").value = "Tester";
        document.getElementById("emailAddress").value = "tim@testing.com";
        document.getElementById("telephoneNumber").value = "01234567890";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/your-address")) {
        document.getElementById("name").value = "ACME Importers Ltd";
        document.getElementById("addressLine1").value = "Unit 42";
        document.getElementById("addressLine2").value = "West Industrial Estate";
        document.getElementById("city").value = "Middlewich";
        document.getElementById("postcode").value = "MD123KD";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/bank-details")) {
        document.getElementById("accountName").value = "ACME Importers Ltd";
        document.getElementById("sortCode").value = "170045";
        document.getElementById("accountNumber").value = "53464584";
        submit();
    }
}
