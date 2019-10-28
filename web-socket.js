'use strict';
document.querySelector('#welcomeForm').addEventListener('submit', connect, true);
document.querySelector('#dialogueForm').addEventListener('submit', sendMessage, true);
var stompClient = null;
var name = null;

function connect(event) {
    name = document.querySelector('#name').value.trim();
    if (name) {
        document.querySelector('#welcome-page').classList.add('hidden');
        document.querySelector('#dialogue-page').classList.remove('hidden');
        var socket = new SockJS('http://localhost:8080/hackanet/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, connectionSuccess);
    }
    event.preventDefault();
}

function connectionSuccess() {
    stompClient.subscribe('/chat/2', onMessageReceived);
}

function sendMessage(event) {
    var messageContent = document.querySelector('#chatMessage').value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            text: document.querySelector('#chatMessage').value,
            sender_id: 2,
            chat_id: 2
        };
        stompClient.send("/chat/2", {}, JSON
            .stringify(chatMessage));
        document.querySelector('#chatMessage').value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    console.log('message type: ' + message.text);
        messageElement.classList.add('message-data');
        var element = document.createElement('i');
        var text = document.createTextNode(message.sender[0]);
        element.appendChild(text);
        messageElement.appendChild(element);
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    document.querySelector('#messageList').appendChild(messageElement);
    document.querySelector('#messageList').scrollTop = document
        .querySelector('#messageList').scrollHeight;
}