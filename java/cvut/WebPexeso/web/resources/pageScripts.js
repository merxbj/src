var fieldState = null;
var CardStateEnum = {
    NOT_TURNED : 0,
    TURNED : 1,
    DISCOVERED : 2,
    DISCOVER_COMMITED : 3
}

function onCardClick(x, y) {
    var clickedCard = fieldState[y][x];
    updateGame(clickedCard);
    updateUi();
}

function updateGame(clickedCard) {
    if (allMovesExceeded()) {
        alert("Pass your turn first!");
        return;
    }

    if (clickedCard.state == CardStateEnum.NOT_TURNED) {
        var anotherCard = getAnotherTurnedCardIfExist();
        if (anotherCard != null) {
            if (cardsMatch(clickedCard, anotherCard)) {
                clickedCard.state = CardStateEnum.DISCOVERED;
                anotherCard.state = CardStateEnum.DISCOVERED;
                return;
            }
        }

        clickedCard.state = CardStateEnum.TURNED;
    }
}

function initializeGame() {
    var gameParams = parseGameParameters();
    buildFieldState(gameParams);
    updateUi();
}

function parseGameParameters() {
    var gameParams = new Array();
    gameParams["fieldState"] = document.getElementById("fieldState").getAttribute("value");
    gameParams["aiState"] = document.getElementById("aiState").getAttribute("value");
    return gameParams;
}

function buildFieldState(gameParams) {
    var serializedFieldState = gameParams["fieldState"];
    deserializeFieldState(serializedFieldState);
}

function deserializeFieldState(serializedFieldState) {
    fieldState = new Array();
    var allCards = serializedFieldState.split(";");
    for (var i = 0; i < allCards.length; i++) {
        serializedCardState = allCards[i].split(",");
        var cardState = deserializeCardState(serializedCardState);
        if (fieldState[cardState.y] == null) {
            fieldState[cardState.y] = new Array();
        }
        fieldState[cardState.y][cardState.x] = cardState;
    }
}

function deserializeCardState(serializedCardState) {
    var cardState = {};
    cardState.x = serializedCardState[0];
    cardState.y = serializedCardState[1];
    cardState.id = serializedCardState[2];
    cardState.state = serializedCardState[3];
    return cardState;
}

function serializeFieldState() {
    var serializedFieldState = "";
    for (var y = 0; y < fieldState.length; y++) {
        for (var x = 0; x < fieldState[y].length; x++) {
            var serializedCard = serializeCard(fieldState[y][x]);
            serializedFieldState += serializedCard + ";";
        }
    }
    return serializedFieldState.substring(0, serializedFieldState.length - 1); // cut away the trailing ';'
}

function serializeCard(card) {
    return card.x + "," + card.y + "," + card.id + "," + card.state;
}

function passTurn() {
    commitAndResetFieldState();
    var serializedFieldState = serializeFieldState();
    document.getElementById("fieldState").setAttribute("value", serializedFieldState);
}

function commitAndResetFieldState() {
    for (var y = 0; y < fieldState.length; y++) {
        for (var x = 0; x < fieldState[y].length; x++) {
            var field = fieldState[y][x];
            if (field.state == CardStateEnum.TURNED) {
                field.state = CardStateEnum.NOT_TURNED;
            } else if (field.state == CardStateEnum.DISCOVERED) {
                field.state = CardStateEnum.DISCOVER_COMMITED;
            }
        }
    }
}

function allMovesExceeded() {
    var turnedCount = 0;
    for (var y = 0; y < fieldState.length; y++) {
        for (var x = 0; x < fieldState[y].length; x++) {
            if (cardHasBeenTouchedThisTurn(fieldState[y][x])) {
                if (++turnedCount == 2) {
                    return true;
                }
            }
        }
    }
    return false;
}

function cardHasBeenTouchedThisTurn(card) {
    return ((card.state == CardStateEnum.TURNED) || (card.state == CardStateEnum.DISCOVERED));
}

function getAnotherTurnedCardIfExist() {
    for (var y = 0; y < fieldState.length; y++) {
        for (var x = 0; x < fieldState[y].length; x++) {
            if (fieldState[y][x].state == CardStateEnum.TURNED) {
                return fieldState[y][x];
            }
        }
    }
    return null;
}

function cardsMatch(card, otherCard) {
    return (card.id == otherCard.id);
}

function updateUi() {
    for (var y = 0; y < fieldState.length; y++) {
        for (var x = 0; x < fieldState[y].length; x++) {
            var card = fieldState[y][x];
            if (card.state >= CardStateEnum.TURNED) {
                document.getElementById('('+x+','+y+')').setAttribute('src',"resources/img/card_" + card.id + ".jpg");
            } else {
                document.getElementById('('+x+','+y+')').setAttribute('src',"resources/img/card_bottom.jpg");
            }
        }
    }
}

/*
function parseUrlParameters() {
    var urlParams = new Array();
    var query = window.location.search.substring(1);
    var parms = query.split('&');
    for (var x = 0; x < parms.length; x++) {
        var pos = parms[x].indexOf('=');
        if (pos > 0) {
            var key = parms[x].substring(0,pos);
            var val = parms[x].substring(pos + 1);
            urlParams[key] = val;
        }
    }
    return urlParams;
}*/