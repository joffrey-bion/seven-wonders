var stompClient = null;

function connect() {
  console.log('Connecting...');
  var socket = new SockJS('/seven-wonders-websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    for (var i = 1; i < 10; i++) {
      subscribeTest(stompClient, '/test' + i);
      subscribeTest(stompClient, '/topic/test' + i);
      subscribeTest(stompClient, '/broadcast/test' + i);
      subscribeTest(stompClient, '/queue/test' + i);
      subscribeTest(stompClient, '/user/queue/test' + i);
      subscribeTest(stompClient, '/user/queue/topic/test' + i);
      subscribeTest(stompClient, '/user/queue/broadcast/test' + i);
    }
  });
}

function sendTest(indexes) {
  for (var i = 0; i < indexes.length; i++) {
    stompClient.send("/app/test" + indexes[i], {}, "test payload " + indexes[i]);
  }
}

function subscribeTest(stompClient, endpoint) {
  var id = endpoint.replace(new RegExp('/', 'g'), '') + '-data';
  $("#test-feeds").append('<tr><td>' + endpoint + '</td><td id="' + id + '">no data received yet</td></tr>');
  stompClient.subscribe(endpoint, function (data) {
    console.log("Received event on " + endpoint + ": data.body=" + data.body);
    $("#" + id).html('<strong>received "' + data.body + '"</strong>');
  });
}

$(function () {
  $("form").on('submit', function (e) {
    e.preventDefault();
  });
  $("#send-test").click(function () {
    var indexesToSend = $("#test-index-field").val().split(',');
    sendTest(indexesToSend);
  });
});

// auto-connect
connect();