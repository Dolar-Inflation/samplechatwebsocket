var stompClient = null;
var username = null;

function initNicknameFromSession() {
    try {
        const stored = sessionStorage.getItem('chat_nickname');
        if (stored) {
            username = stored;
            const nickInput = document.querySelector("#nickname_input_value");
            if (nickInput) {
                nickInput.value = username;
                nickInput.setAttribute('readonly', 'readonly');
            }
        }
    } catch (e) {
        console.warn('Не удалось получить nickname из sessionStorage', e);
    }
}

function connect() {
    if (!username) {
        const input = document.querySelector("#nickname_input_value");
        username = input ? input.value.trim() : null;
    }
    if (!username) {
        alert("Введите никнейм!");
        return;
    }

    if (stompClient && stompClient.connected) {
        console.log("already connected");
        return;
    }

    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function(frame) {
        console.log("connected " + frame);
        stompClient.subscribe('/topic/messages', function(response) {
            const body = response && response.body;
            if (!body) return;
            const firstChar = body.trim()[0];
            if (firstChar !== '{' && firstChar !== '[') {
                console.warn('Non-JSON WS message:', body);
                return;
            }
            try {
                var data = JSON.parse(body);
                draw("left", data.message, data.username);
            } catch (e) {
                console.error('Failed to parse WS JSON:', e, body);
            }
        });
        stompClient.send("/app/addUser", {}, JSON.stringify({
            'username': username,
            'message': username + " joined the chat"
        }));
    }, function(error) {
        console.error('STOMP connect error', error);
        stompClient = null;
        // можно добавить авто‑переподключение при желании
    });
}

function draw(side, text, username) {
    console.log("drawing..");
    var $message;
    $message = $($('.message_template').clone().html());
    $message.addClass(side).find('.text').html(text);
    $message.find('.username').html(username);
    $('.messages').append($message);
    return setTimeout(function () {
        return $message.addClass('appeared');
    }, 0);
}

function disconnect() {
    if (stompClient) {
        stompClient.disconnect();
        stompClient = null;
    }
}

function sendMessage() {
    const message = $("#message_input_value").val();
    const nick = username || $("#nickname_input_value").val();
    if (!message || !nick) {
        alert('Введите сообщение и никнейм');
        return;
    }
    stompClient.send("/app/message", {}, JSON.stringify({
        'message': message,
        'username': nick
    }));
    $("#message_input_value").val('');
}

document.addEventListener('DOMContentLoaded', function() {
    initNicknameFromSession();
    // Автоподключение, если ник уже есть (после логина)
    if (sessionStorage.getItem('chat_nickname')) {
        connect();
    }
});