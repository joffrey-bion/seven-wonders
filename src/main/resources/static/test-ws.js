var stompClient = null;

function connect() {
  console.log('Connecting...');
  var socket = new SockJS('/seven-wonders-websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
  });
}

function send(endpoint, payload) {
    stompClient.send(endpoint, {}, payload);
}

function subscribeTo(endpoint) {
  $("#test-feeds").prepend('<tr><td>' + endpoint + '</td><td>Subscribed</td></tr>');
  stompClient.subscribe(endpoint, function (data) {
    $("#test-feeds").prepend('<tr><td>' + endpoint + '</td><td>Received: <pre>' + data.body + '</pre></td></tr>');
  });
}

$(function () {
  $("form").on('submit', function (e) {
    e.preventDefault();
  });
  $("#send-btn").click(function () {
    var endpoint = $("#path-field").val();
    var payload = $("#payload-field").val();
    send(endpoint, payload);
  });
  $("#subscribe-btn").click(function () {
    var endpoint = $("#subscribe-path-field").val();
    subscribeTo(endpoint);
  });
});

// auto-connect
connect();