// auth.js

const SOCKET_ENDPOINT = '/websocket';
const REGISTER_DEST = '/app/RegisterUser';
const LOGIN_DEST = '/app/LoginUser';
const SUBSCRIBE_TOPIC = '/topic/messages';

let stompClient = null;
let reconnectInterval = 5000;

// UI элементы (обновлённые id)
const regForm = document.getElementById('reg-form');
const loginForm = document.getElementById('login-form');

const regUsername = document.getElementById('reg-username');
const regPassword = document.getElementById('reg-password');

const loginUsername = document.getElementById('login-username');
const loginPassword = document.getElementById('login-password');

function showToast(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
}

function connect() {
    const socket = new SockJS(SOCKET_ENDPOINT);
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function(frame) {
        showToast('Connected to WebSocket', 'success');
        stompClient.subscribe(SUBSCRIBE_TOPIC, function(message) {
            if (!message.body) return;
            try {
                const payload = JSON.parse(message.body);
                handleServerMessage(payload);
            } catch (e) {
                console.error('Invalid message from server', e);
            }
        });
    }, function(error) {
        showToast('WebSocket connection lost, retrying...', 'error');
        stompClient = null;
        setTimeout(connect, reconnectInterval);
    });
}

function handleServerMessage(payload) {
    showToast('Message from server: ' + JSON.stringify(payload), 'info');

    // Ожидаем, что сервер вернёт объект с полем username при успешной регистрации/логине
    if (payload.username) {
        try {
            sessionStorage.setItem('chat_nickname', payload.username);
        } catch (e) {
            console.warn('sessionStorage недоступен', e);
        }
        window.location.href = '/index.html';
    }
}

function sendRegisterDTO() {
    const username = regUsername.value.trim();
    const password = regPassword.value;

    if (!username || !password) {
        showToast('Заполните все поля регистрации', 'error');
        return;
    }

    const usersDTO = {
        username: username,
        password: password
    };

    if (!stompClient || !stompClient.connected) {
        showToast('Нет соединения с сервером. Попробуйте позже.', 'error');
        return;
    }

    stompClient.send(REGISTER_DEST, {}, JSON.stringify(usersDTO));
    showToast('Запрос на регистрацию отправлен', 'info');
}

function sendLogin() {
    const username = loginUsername.value.trim();
    const password = loginPassword.value;

    if (!username || !password) {
        showToast('Заполните все поля входа', 'error');
        return;
    }

    const loginDTO = {
        username: username,
        password: password
    };

    if (!stompClient || !stompClient.connected) {
        showToast('Нет соединения с сервером. Попробуйте позже.', 'error');
        return;
    }

    stompClient.send(LOGIN_DEST, {}, JSON.stringify(loginDTO));
    showToast('Запрос на вход отправлен', 'info');
}

if (regForm) {
    regForm.addEventListener('submit', function(e) {
        e.preventDefault();
        sendRegisterDTO();
    });
}

if (loginForm) {
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        sendLogin();
    });
}

document.addEventListener('DOMContentLoaded', function() {
    connect();
});