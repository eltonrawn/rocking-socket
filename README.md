# Rocking Socket

# How To Use It:
Server Side:
1. First a server has to listen to a port always to serve clients. For that, open "Executable-jar-fies/ServerFx.jar".
2. Then a window will open asking for a port number. So basically what it means is, a server will run in that local machine's port number with it's ip address.
3. After that, another window will open where two buttons can be seen.
4. With "Create User" button, users can be created for application.
5. With Manage Chat Room two things can be done. Adding chat rooms to application and assigning users to those chat rooms can be done.

Client Side:
1. First, open "Executable-jar-fies/ClientFx.jar". 
2. A window will open asking for ip address and port number where server resides.
3. Upon entering, another window will open asking for username and password.
4. Upon successful login, a window will open with multiple chat rooms in which this user has permission.
5. Enjoy chat.

Be aware:
1. Server should run before any client can enter.
2. If any chat room is added. Changes won't reflect to running clients. Client has to close application and login again to see the changes.
3. Any changes (Ex. chat/user creation ) made in server is not permanent. Closing server will make those changes go away. Saving server configuration is not available in this build.