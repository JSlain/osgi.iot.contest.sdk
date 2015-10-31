# 

${Bundle-Description}

Here's the way it works.
The path to follow is calculated everytime a Locator is reached.
That way, if a Locator didn't successfully capture the rfid signal, the train will continue to follow a successfull path to its target (maybe doing another turn around the entire map...).

The path to follow is recalculated everytime an access requested has been denied; the new path excludes the denied track.

As an extra to manage missed rfid detection, if the NEXT locator to be reach is the target segment, but the observation we receive is another one ON THE SAME TRACK, the train will SLOWLY move backward, searching for the target.
 

## Example

## Configuration

	Pid: org.jslain.trains.train.manager
	
	Field					Type				Description
		
	
## References

