/*
    Validates the user input to the form. It validates all the fields separately
    and keeps going even thought validation errors already occured and builds an
    error message.
    This error message is then finally shown to the user describing all the input
    rules he violated.
*/
function validateInput() {
    /*
        Acquire all fields from the form. I know, this is ugly but this is
        my very first experience with the JavaScript and with the HTML DOM.
        Please, use less offensive words than in usual case when you see this.
        I really don't like this either!
    */
    var departureDateField      = document.getElementById("date");
    var departureTimeField      = document.getElementById("time");
    var stage1Field             = document.getElementById("stage1");
    var stage2Field             = document.getElementById("stage2");
    var stage3Field             = document.getElementById("stage3");
    var stage4Field             = document.getElementById("stage4");
    var stage5Field             = document.getElementById("stage5");
    var stage6Field             = document.getElementById("stage6");
    var stage7Field             = document.getElementById("stage7");
    var includeNoteField        = document.getElementById("includeNote"); 
    var noteField               = document.getElementById("note");
    var loginField              = document.getElementById("login");
    var passwordField           = document.getElementById("password");
    var secretCodeField         = document.getElementById("secretCode");
    
    /*
        Make sure that we have got all the fields!
    */
    if ((departureDateField == null)        || (departureTimeField == null) ||
        (stage2Field == null)               || (stage3Field == null)        ||
        (stage4Field == null)               || (stage5Field == null)        ||
        (stage6Field == null)               || (stage7Field == null)        ||
        (includeNoteField == null)          || (noteField == null)          ||
        (loginField == null)                || (passwordField == null)      ||
        (secretCodeField == null)           || (stage1Field == null)) {
            /*  I believe that his should never happen! If yes, 
                it is probably the developer mistake! */
            alert("Unexpected error occured during the form validation!");
            return false;
    }
    
    /*
        Lets validate all fields one by one to be able to inform the user about the
        wrong inputs to provide him with friendly information how to correct them.
    */
    var errorMessage = "";
    
    if (!validateDate(departureDateField.value)) {
        errorMessage += "Chybné datum odjezdu! Požadovaný formát MM/DD/YYYY\n";
    }
    
    if (!validateTime(departureTimeField.value)) {
        errorMessage += "Chybný čas odjezdu! Požadovaný formát HH:MM:SS\n";
    }
    
    if (!validateNumber(stage1Field.value)) {
        errorMessage += "Chybný čas u úseku č. 1!\n";
    }
    
    if (!validateNumber(stage2Field.value)) {
        errorMessage += "Chybný čas u úseku č. 2!\n";
    }
    
    if (!validateNumber(stage3Field.value)) {
        errorMessage += "Chybný čas u úseku č. 3!\n";
    }
    
    if (!validateNumber(stage4Field.value)) {
        errorMessage += "Chybný čas u úseku č. 4!\n";
    }
    
    if (!validateNumber(stage5Field.value)) {
        errorMessage += "Chybný čas u úseku č. 5!\n";
    }
    
    if (!validateNumber(stage6Field.value)) {
        errorMessage += "Chybný čas u úseku č. 6!\n";
    }
    
    if (!validateNumber(stage7Field.value)) {
        errorMessage += "Chybný čas u úseku č. 7!\n";
    }

    if (isRegistered()) {
        if ((trim(loginField.value) == "") || (trim(passwordField.value) == "")) {
            errorMessage += "Chybné jméno či heslo!\n";
        }
    } else {
        if ((trim(secretCodeField.value) == "")) {
            errorMessage += "Chybný tajný kód!\n";
        }
    }
    
    if (errorMessage != "") {
        alert(errorMessage);
    }
    
    return (errorMessage == "");
}

/*
    Simply validates the date to be in format MM/DD/YYYY
*/
function validateDate(date) {
    if (date.length != 10) {
        return false;
    }
    
    if ((date.charAt(2) != "/") || (date.charAt(5) != "/")) {
        return false;
    }
    
    if (!validateNumberRange(date.substring(0,2), 1, 12) ||
            !validateNumberRange(date.substring(3,5), 1, 31) ||
            !validateNumberRange(date.substring(6,10), 1970, 2100)) {
        return false;
    }
    
    return true;
    
}

/*
    Simply validates the time to be in format HH:MM:SS
*/
function validateTime(time) {
    if (time.length != 8) {
        return false;
    }
    
    if ((time.charAt(2) != ":") || (time.charAt(5) != ":")) {
        return false;
    }
    
    if (!validateNumberRange(time.substring(0,2), 0, 23) ||
            !validateNumberRange(time.substring(3,5), 0, 59) ||
            !validateNumberRange(time.substring(6,8), 0, 59)) {
        return false;
    }
    
    return true;
}

/*
    Goes through the given string and returns false on first occurence
    different from digit. If pass all through, returns true.
*/
function validateNumber(number) {
    if ((number == null) || (number == "")) {
        return false;
    }
    
    for (var i = 0; i < number.length; i++) {
        if ((number.charAt(i) < "0") || (number.charAt(i) > "9")) {
            return false;
        }
    }

    return true;
}

/*
    Utilizes the function validateNumber(number) to verify that
    the given string is a number and if yes, verifies whether it lies within
    the given bounds (inclusively)
*/
function validateNumberRange(number, min, max) {
    if (validateNumber(number)) {
        num = parseInt(number);
        return (num != null) ? ((num >= min) && (num <= max)) : false;
    }

    return false;
}

/*
    Iterates over the array of radio buttons indicating whether the user
    selected the "registered user" or "unregistered user" and returns
    true if "registered" is the option, false otherwise.
*/
function isRegistered() {
    var radioGroup = document.forms["inputForm"].registered;
    if (radioGroup == null) {
        return false;
    }
    for (var i = 0; i < radioGroup.length; i++) {
        if (radioGroup[i].checked) {
            return (radioGroup[i].value == "registered");
        }
    }
    /* Actually, we should never get there! */
    return false;
}

/*
    Just trims out the leading and trailing spaces.
*/
function trim(string) {
    if ((string == null) || (string == "")) {
        return "";
    }
    
    var i = 0;
    var j = string.length - 1;
    
    while (string.charAt(i) == " ") {i++;}
    while (string.charAt(j) == " ") {j--;}
    
    return (i <= j) ? string.substring(i, j + 1) : "";
}

/*
    Handles the click on the text area. If the text present there matches the
    condition, it means that it contains only the help text which is intended 
    to be deleted.
*/
function onTextAreaClick() {
    if (document.forms["inputForm"].note.value.indexOf("Zde napište poznámku ...") != -1) {
        document.forms["inputForm"].note.value = "";
    }
}

/*
    Handles the lost of focus on the text area. If the text present there matches
    the condition (is empty), it means that it should be filled again with the help
    text.
*/
function onTextAreaBlur() {
    if (document.forms["inputForm"].note.value == "") {
        document.forms["inputForm"].note.value = "Zde napište poznámku ...";
    }
}

/*
    Let's fill the text area here to prevent of adding extra spaces from the XHTML code.
*/
function onDataInputBodyLoad() {
    /* Call the default implementation at first */
    onBodyLoad();
    
    if (document.forms["inputForm"].note != null) {
        document.forms["inputForm"].note.value = "Zde napište poznámku ...";
        document.forms["inputForm"].note.disabled = true;
    }

}

/*
    Changes the css skin.
*/
function onChangeSkin(name) {
    
    changeSkin(name); // change
    setStyleSkin(name); // persist
    return false; // ensure not following the link clicked
}


function changeSkin(name) {
    var links = document.getElementsByTagName("link");
    if (links != null) {
        for (var i = 0; i < links.length; i++) {
            if ((links[i].rel.indexOf("stylesheet") != -1) && (links[i].title != null)) {
                links[i].disabled = (links[i].title != name);
            }
        }
    }
}

function setStyleSkin(value) {
    var cookie = "skin";
    cookie += "=";
    cookie += encodeURIComponent(value);
    cookie += "; max-age=";
    cookie += 24 * 60 * 60 * 30; // 30 days
    cookie += "; path=/";
    
    document.cookie = cookie; 
}

function getStyleSkin() {
    var cookie = document.cookie;
    if ((cookie != null) && (cookie != "")) {
        var splits = cookie.split(";");
        for (var i = 0; i < splits.length; i++) {
            if (splits[i].indexOf("skin=") != -1) {
                var skinCookie = splits[i].split("=");
                return skinCookie[1];
            }
        }
    }
    return "blue"; // this is our default skin!
}

function onBodyLoad() {
    changeSkin(getStyleSkin());
}

function onIncludeNoteClick() {
    var includeNote = document.forms["inputForm"].includeNote;
    var note = document.forms["inputForm"].note;
    
    if ((includeNote == null) || (note == null)) {
        return;
    }
    
    note.disabled = !includeNote.checked;
}

function onRegisteredClick() {
    
    var password = document.forms["inputForm"].password;
    var login = document.forms["inputForm"].login;
    var secretCode = document.forms["inputForm"].secretCode;
    
    if ((password == null) || (login == null) || (secretCode == null)) {
        return;
    }
    
    var registered = isRegistered();
        
    login.disabled = !registered;
    password.disabled = !registered;
    secretCode.disabled = registered;
    
}