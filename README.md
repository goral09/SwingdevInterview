# Game

[ ![Codeship Status for goral09/SwingdevInterview](https://codeship.io/projects/ef178ec0-0787-0132-8774-7a8fe1d63f6e/status)](https://codeship.io/projects/31270)


This project is an interview step into Swingdev company. Task is to create two army war simulation with probability factor.

## Idea

Idea is to create it using Akka Actors. Each soldier is represented by one Actor. Each army can consists of arbitrary amount of soldiers. 

## Soldier

There are three types of soldiers:

- Archer
- Knight
- Horserider

Each type of soldier has its unique abilities. We assume that world where game takes place is arbitrary big array NxM each soldier occupies one square. They also have something like 'visibility range'. This range is represented by a number of squares it can spot enemies around him. 

## Design

![design](https://lh6.googleusercontent.com/-kzGeYtJJJH0/U-nbnnL-WII/AAAAAAAADG0/La7Zx1_aYS0/s1600/diagram1.png)

Actor can make two types of moves:

1. Move


	If it wants to move it sends new desired location to **WorldActor**. **World Actor** validates decision. If new place on map is empty it sets soldier's location to it and sends back:

	- Confirmation
	- Position
	- List of enemies in it's range together with their _ActorRefs_
	- VectorClock

2. Attack


	Soldier can attack only after verifying there are enemies in his visibility range. If there are it sends:

	- Attack command
	- VectorClock



## Running

    sbt run

Neat!
