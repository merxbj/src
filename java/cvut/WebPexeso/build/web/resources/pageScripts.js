var fieldState = null;
var CardStateEnum = {
    NOT_TURNED : 0,
    TURNED : 1,
    DISCOVERED : 2
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
    if (allMovesExceeded()) {
        var serializedFieldState = serializeFieldState();
        document.getElementById("fieldState").setAttribute("value", serializedFieldState);
        return true;
    } else {
        alert("You have to turn two cards!");
        return false;
    }
}

function allMovesExceeded() {
    var turnedCount = 0;
    for (var y = 0; y < fieldState.length; y++) {
        for (var x = 0; x < fieldState[y].length; x++) {
            if (fieldState[y][x].state == CardStateEnum.TURNED) {
                if (++turnedCount == 2) {
                    return true;
                }
            }
        }
    }
    return false;
}

function updateUi() {
    for (var y = 0; y < fieldState.length; y++) {
        for (var x = 0; x < fieldState[y].length; x++) {
            var card = fieldState[y][x];
            if (card.state == CardStateEnum.TURNED) {
                document.getElementById('('+x+','+y+')').setAttribute('src',"resources/img/card_" + card.id + ".jpg");
            } else if (card.state == CardStateEnum.DISCOVERED) {
                document.getElementById('('+x+','+y+')').setAttribute('src',"resources/img/card_discovered.jpg");
            } else {
                document.getElementById('('+x+','+y+')').setAttribute('src',"resources/img/card_bottom.jpg");
            }
        }
    }
}

function onNewGameLoad() {
    for (var i = 1; i <= 4; i++) {
        updatePlayerInputEnableness(i);
    }
    
}

function updatePlayerInputEnableness(playerNumber) {
    if (document.getElementById("PlayerEnabled"+playerNumber).checked) {
        document.getElementById("PlayerType"+playerNumber).disabled = false;
        document.getElementById("PlayerName"+playerNumber).disabled= false;
    } else {
        document.getElementById("PlayerType"+playerNumber).disabled = true;
        document.getElementById("PlayerName"+playerNumber).disabled= true;
    }
}