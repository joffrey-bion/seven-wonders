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

    stompClient.subscribe('/user/queue/errors', function (msg) {
      var error = JSON.parse(msg.body);
      console.error(error);
    });

    stompClient.subscribe('/topic/games', function (msg) {
      var game = JSON.parse(msg.body);
      if (Array.isArray(game)) {
        console.log("Received new games: " + game);
        for (var i = 0; i < game.length; i++) {
          addNewGame(game[i]);
        }
      } else {
        console.log("Received new game: " + game);
        addNewGame(game);
      }
    });

    stompClient.subscribe('/user/queue/join-game', function (msg) {
      var game = JSON.parse(msg.body);
      console.log("Joined game: " + game);
      addNewPlayer(game);
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

function sendCreateGame(gameName, playerName) {
  stompClient.send("/app/lobby/create-game", {}, JSON.stringify({
    'gameName': gameName,
    'playerName': playerName
  }));
}

function sendJoinGame(gameName, playerName) {
  stompClient.send("/app/lobby/join-game", {}, JSON.stringify({
    'gameName': gameName,
    'playerName': playerName
  }));
}

function addNewGame(game) {
  console.log(game);
  $("#game-list-content").append('<tr><td>' + game.name + '</td><td><button id="join-' + game.id +
          '" type="submit">Join</button></td></tr>');
  $("#join-" + game.id).click(function () {
    sendJoinGame(game.name, $("#player-name-field").val());
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
    sendCreateGame($("#game-name-field").val(), $("#player-name-field").val());
  });
});