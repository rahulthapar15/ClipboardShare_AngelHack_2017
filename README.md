# ClipSync
##### Angelhack 2017

#### Problem Statement
```
Reduce the effort of transfering small text from mobile/desktop to desktop/mobile.
Simply Ctrl+C on your desktop and directly paste it on your phone.

```
##### How does it work?

1. The desktop client and the android app both constantly listen to the clipboard. As soon as something is updated (Updated means something new has
 replaced the old clipping) the desktop stores it in a buffer and sends it to the server. The android app fetches the newly added clipping
 and overwrites the existing clipping on the phone clipboard.
 
2. The Application maintains a history of all the clippings so that they can be used later as well.

 > Widget support is added to directly use the clippings.
 
 ##### SCREENSHOTS
 ---
 

