# minecraft-evolution [![Build Status](https://semaphoreci.com/api/v1/projects/b5658be0-0e05-4ae2-95df-759fd9561ab7/2717468/badge.svg)](https://semaphoreci.com/voxelbee-87/minecraft-evolution)
A system to write evolving AI within the game minecraft

## To Do
- [x] Login to minecraft server
- [x] Get all  packets working
- [ ] Enable other minecraft versions
- [ ] Add physics and movement code for the player

## How to get running?
1. Clone the repository then run the command `gradlew build`
2. Open eclipse and then import existing gradle project
3. Start coding

## Setting up the minecraft server
1. Navigate to the server directory
2. run the command `java -jar BuildTools.jar --rev 1.14.1` you can change the version to be what ever you want
3. Update the run.bat file if you changed the Minecraft version to the new version
4. Run the server and except the eula
5. Edit server.txt file and set online mode to false
