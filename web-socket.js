'use strict';
document.querySelector('#welcomeForm').addEventListener('submit', connect, true);
document.querySelector('#dialogueForm').addEventListener('submit', sendMessage, true);
var stompClient = null;
var name = null;
var k = 0;

function connect(event) {
    name = document.querySelector('#name').value.trim();
    if (name) {
        document.querySelector('#welcome-page').classList.add('hidden');
        document.querySelector('#dialogue-page').classList.remove('hidden');
        var socket = new SockJS('http://localhost:8080/hackanet/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, connectionSuccess);
    }
    console.log('connecting');
    event.preventDefault();
}

function connectionSuccess() {
    k = 0;
    stompClient.send("/chat/3/connect", {}, "connected");
    stompClient.subscribe('/chat/3', onMessageReceived);
}

function sendMessage(event) {
    var messageContent = document.querySelector('#chatMessage').value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            text: document.querySelector('#chatMessage').value,
            sender_id: 2,
            chat_id: 2
        };
        stompClient.send("/chat/3/send", {}, JSON
            .stringify(chatMessage));
        document.querySelector('#chatMessage').value = '';
    }
    console.log('sending message');
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    console.log('message type: ' + message.text);
    if (k===0) {
        for (var i = 0; i < message.length; i++) {
            messageElement.classList.add('message-data');
            // var element = document.createElement('i');
            // messageElement.appendChild(element);
            var usernameElement = document.createElement('span');
            var usernameText = document.createTextNode(message[i].sender.name);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
            var textElement = document.createElement('p');
            var messageText = document.createTextNode(message[i].text);
            textElement.appendChild(messageText);
            messageElement.appendChild(textElement);
            var imageElement = document.createElement('img');
            imageElement.setAttribute('src', message[i].attachments[0].preview_link);
            messageElement.appendChild(imageElement);
            document.querySelector('#messageList').appendChild(messageElement);
            document.querySelector('#messageList').scrollTop = document
                .querySelector('#messageList').scrollHeight;
        }
    } else {
        messageElement.classList.add('message-data');
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender.name);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.text);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
        document.querySelector('#messageList').appendChild(messageElement);
        document.querySelector('#messageList').scrollTop = document
            .querySelector('#messageList').scrollHeight;
    }
    k++;
}