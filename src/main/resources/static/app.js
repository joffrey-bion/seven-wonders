var stompClient = null;

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#game-list").show();
  } else {
    $("#game-list").hide();
  }
  $("#greetings").html("");
}

function connect() {
  var socket = new SockJS('/seven-wonders-websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    setConnected(true);
    console.log('Connected: ' + frame);

    stompClient.subscribe('/broadcast/games', function (gameId) {
      console.log("Received new game: " + gameId);
      addNewGame(gameId.body);
    });

    stompClient.subscribe('/broadcast/players', function (player) {
      console.log("Received new player: " + player);
      addNewPlayer(JSON.parse(player.body));
    });
  });
}

function disconnect() {
  if (stompClient != null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

function sendCreateGame() {
  stompClient.send("/app/lobby/create-game", {}, "");
}

function sendJoinGame(gameId) {
  stompClient.send("/app/lobby/join-game", {},
          JSON.stringify({'gameId': gameId, 'playerName': $("#player-name-field").val()}));
}

function addNewGame(gameId) {
  console.log(gameId);
  $("#game-list-content").append('<tr><td>' + gameId + '</td><td><button id="join-' + gameId + '" type="submit">Join</button></td></tr>');
  $("#join-" + gameId).click(function () {
    sendJoinGame(gameId);
  });
}

function addNewPlayer(player) {
  console.log(player);
}

$(function () {
  $("form").on('submit', function (e) {
    e.preventDefault();
  });
  $("#connect").click(function () {
    connect();
  });
  $("#disconnect").click(function () {
    disconnect();
  });
  $("#create-game").click(function () {
    sendCreateGame();
  });
  $("#join-game").click(function () {
    sendJoinGame();
  });
});