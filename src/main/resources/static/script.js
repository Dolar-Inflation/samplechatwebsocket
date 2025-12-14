
var stompClient = null;
var username = null;

function initNicknameFromSession() {
    try {
        const stored = sessionStorage.getItem('chat_nickname');
        if (stored) {
            username = stored;
            // подставляем в поле, делаем его readonly чтобы пользователь видел ник
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

function connect(){
    // если username уже задан из sessionStorage — используем его,
    // иначе читаем из поля ввода
    if (!username) {
        username = document.querySelector("#nickname_input_value").value;
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
    stompClient.connect({},function (frame) {
        console.log("connected " + frame);
        stompClient.subscribe('/topic/messages', function (response) {
            var data = JSON.parse(response.body);
            draw("left", data.message, data.username);
        });
        stompClient.send("/app/addUser", {}, JSON.stringify({
            'username': username,
            'message': username + " joined the chat"
        }));
    }, function(error) {
        console.error('STOMP connect error', error);
    });
}

function draw(side,text,username){
    console.log("drawing..")
    var $message;
    $message = $($('.message_template').clone().html());
    $message.addClass(side).find('.text').html(text);
    $message.find('.username').html(username);
    $('.messages').append($message);
    return setTimeout(function () {
        return $message.addClass('appeared');
    }, 0);
}

function disconnect(){
    if (stompClient) {
        stompClient.disconnect();
        stompClient = null;
    }
}

function sendMessage(){
    const message = $("#message_input_value").val();
    // используем username из sessionStorage или поле
    const nick = username || $("#nickname_input_value").val();
    if (!message || !nick) {
        alert('Введите сообщение и никнейм');
        return;
    }
    stompClient.send("/app/message", {}, JSON.stringify({
        'message': message,
        'username': nick
    }));
    // очистим поле сообщения
    $("#message_input_value").val('');
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    initNicknameFromSession();
    // если хотите автоматически подключаться при наличии ника:
    // if (sessionStorage.getItem('chat_nickname')) connect();
});