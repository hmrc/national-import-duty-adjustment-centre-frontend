
// =====================================================
// Back link mimics browser back functionality
// =====================================================
// store referrer value to cater for IE - https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/10474810/  */
const docReferrer = document.referrer

// prevent resubmit warning
if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
  window.history.replaceState(null, null, window.location.href);
}

// automatically submit document upload on file select
function onFileSelect() {
  try{
    // NF-403 - hide the form controls, display the spinner and disable the 'Continue' button before submitting
    document.getElementById("upload-form").className = "govuk-!-display-none";
    document.getElementsByClassName("hidden-spinner-row").item(0).className = "govuk-summary-list__row";
    document.getElementById("nidac-continue").disabled = true;
  }
  finally {
    document.getElementById("upload-form").submit();
  }
}