package pacman;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;

public class DataTester {

	public static void main(String... args) {
		DataTuple[] tuples = DataSaverLoader.LoadPacManData();
		int counter = 0;
		for (DataTuple d : tuples) {
			System.out.println("Normal: "+d.getSaveString());
			System.out.print("Discrete: "+d.DirectionChosen + ", " + d.mazeIndex + ", " + d.currentLevel + ", " + d.discretizePosition(d.pacmanPosition) + ", "
					+ d.pacmanLivesLeft + ", " + d.discretizeCurrentScore(d.currentScore) + ", " + d.discretizeCurrentLevelTime(d.currentLevelTime) + ", "
					+ d.discretizeNumberOfPills(d.numOfPillsLeft) + ", " + d.discretizeNumberOfPowerPills(d.numOfPowerPillsLeft) + ", " + d.isBlinkyEdible
					+ ", " + d.isInkyEdible + ", " + d.isPinkyEdible + ", " + d.isSueEdible + ", " + d.discretizeDistance(d.blinkyDist) + ", "
					+ d.discretizeDistance(d.inkyDist) + ", " + d.discretizeDistance(d.pinkyDist) + ", " + d.discretizeDistance(d.sueDist)
					+ ", " + d.blinkyDir+ ", " + d.inkyDir+ ", " + d.pinkyDir+ ", " + d.sueDir + "\n");
			counter++;
			if (counter>100) break;
		}

	}

}
