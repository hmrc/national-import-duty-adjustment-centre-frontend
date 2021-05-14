
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
    // NF-403 - hide the form controls, display the progress bar and disable the 'Continue' button before submitting
    document.getElementById("nidac-continue").disabled = true;
    document.getElementById("upload-form").classList.add("govuk-!-display-none");
    document.querySelector(".hidden-progress-row").classList.remove("govuk-!-display-none");
    var errorBlock = document.querySelector(".govuk-error-summary");
    if(errorBlock){
      errorBlock.classList.add("govuk-!-display-none");
    }
  }
  finally {
    // timeout before submitting is to give Safari time to render un-hidden element
    // animations stop in Safari once form is submitted so animation start is delayed by 500 ms
    setTimeout(() => {  document.getElementById("upload-form").submit(); }, 500);
  }
}