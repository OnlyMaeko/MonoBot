## MonoBot

CS 544 General ReadME:

Author: Marko Melishchuk

Stateful Protocol Goals
- Marko Melishchuk CS 544 Final Project: The goal of the assignment is to implement a custom protocol application layer protocol by writing a server and a client run over QUIC to send packets back and forth between to play Monopoly

- Grading "not concerns" but guidance, the game application itself is buggy, there is quite a bit of logic that is hardcoded and doesn't reflect the application protocol, it is a result of the fact that this iteration of Monobot deviates quite heavily from the initial intention of the application (which is also unfinished). The initial application (ReadME can be found below), was to build a machine learning algorithm to 

- Please do not grade based on UI quality (unless you think its awesome), the original iteration wasn't coded with a UI in mind, thats on the 2027 to do list. The general logic from the ML algorithm and game state implementation was the base for the multiplayer implementation. Not everything has been transferred immediately to player options, I'm sure there are many things I have forgotten in this iteration that are very buggy and potentially session ending, they should not be a result of the PDUs or the DFA, simply a result of the poor (ish, im trying to be nicer to it) quality of the adapted multiplayer version. PDUs and DFA flow can be found in the following files: server.py, client.py, bridge.py, State.java, Server.java, Client.java. The rest of the java and class files are solely meant to implement the application and while there are some game state things that can be found in interface it is largely meant to only cover the actual interface and running of the local gamestate, not the transmission. The payload consists of a JSON file that contains the entire game state to be updated locally at the client. For reasons listed above the output in the client console may not exactly match the visual representation as the bot iteration was fully made for console interaction and the 


( Prof Parkingson: If anything does not work for any reason please reach out to me at my drexel email, the instructions should work and since the bulk of the grade is a successful run and it has been tested on a fresh machine it should work but I should be available to troubleshoot any potential issues)

- Implementation instructions: 

    WSL Install Command
    wsl --install -d Ubuntu-22.04

    Restart your machine

    wsl -d Ubuntu-22.04

    (If you have multiple wsl distros the command is different, I believe)

    Directory Nav that files will go into
    cd insert your path (Path that I used in the video during first implementation is cd ~ then running the git clone)

    At this stage you may need to sudo install git, as shown in the implementation video depending on if you already have git installed or not and that can be done with this command:
    sudo apt install git -y

    Git clone
    git clone https://github.com/OnlyMaeko/MonoBot.git

    Sudo Update Command (you can do this at any point and may be preferable to do at the start but it should be functional in this stage)
    sudo apt update


    (If there is trouble running any commands I would recommend running the Python repos, QUIC, and KeyGen commands before the git clone but there shouldn't be)
    Python repos
    sudo apt install python3 python3-pip default-jdk openssl build-essential libssl-dev python3-dev -y

    QUIC Library
    pip3 install aioquic

    KeyGen
    openssl req -x509 -newkey rsa:2048 -nodes -keyout key.pem -out cert.pem -days 365 -subj '/CN=localhost'

    

    Compile Java Files, run in Terminal 1
    javac *.java

    Kill zombies (Only necessary for restarting the python servers, to be run after every )
    fuser -k 4433/udp && fuser -k 8080/tcp && fuser -k 9090/tcp

    Start the Server and Client QUIC bridges (Copy and run all three commands at once in Terminal 1)
    python3 server.py &
    python3 client.py &
    java Server

    In a second Terminal:
    java Client


Above instructions should work but please refer to the demo video listed below for a (not so clean) implementation of the repo and protocol


Youtube video link: https://youtu.be/NUmI53HbyJM


General MonoBot ReadME
Authors: Michael Wallison & Marko Melishchuk

Project Description: MonoBot is a machine learning project with the goal to create a fully functioning and interactive bot to play monopoly against. Over the course of the project we aim to build a program to “learn” how to play monopoly. Upon completion of learning we will create an interface for a player to play against versions of the bot. The versions will consist of the most advanced generation as well as versions with hand tailored personalities. The final product will include an interface that allows a user to pit bots against one another to test strategies and view how they play against each other.

Machine Learning Goals: 
- Create a functional bot that plays monopoly against a bot of the same strength. We are currently testing our infrastructure and are closing in on completing step one.

- Create a way to evaluate a game state at any point and then make decisions based on said game state evaluation.

- Construct a formula to evaluate games to see which decisions influenced the outcome. Then create a model to influence decision making in favor of positive decisions.

- Construct tournaments to build the decision making to a very advanced level of play.

- Upon generating the “best” version create an interface to allow a user to play against the bot as well as set bots against one another.