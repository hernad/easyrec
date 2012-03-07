// Preloads the animated waiting icon
//
waitingImage = '<img alt="wait" src="' + webappPath + 'img/wait16.gif"/>';

if (document.images) {
    picWait = new Image(16, 16);
    picWait.src = webappPath + "img/wait16.gif";
}