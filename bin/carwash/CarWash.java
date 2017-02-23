import java.util.ArrayList;
import carwash.Car;
import carwash.Queue;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CarWash extends Application {

	public static int totalMins, hours, minutes, numCarsInQueue, carsWashed, idleTimeTotal, waitAverage,
			longestWaitTime, carsBypassed, currentTime, arrivalTime, waitTime, serviceTime, serviceStartTime,
			departTime, waitTotal, longestWait;

	public static Car currentCar, prevCar, tempCar, tempCar2, lastCar, newCar;

	public static String customersBypassedString, numServedString, timeIdleString, avgWaitString, longestWaitString,
			longestWaitTimeString;

	public static Queue<Car> carsWaiting = new Queue();

	public static ArrayList<Integer> waitTimes = new ArrayList<>();

	public static void main(String[] args) {

		launch(args);

	}

	@Override
	public void start(Stage primaryStage) {
		Stage window = primaryStage;
		Font mainFont = new Font("Verdana", 45);
		Font textFont = new Font("Verdana", 20);

		window.setTitle("Ethan's Car Wash");

		BorderPane mainScreen = new BorderPane();
		mainScreen.setPadding(new Insets(20, 20, 20, 20));
		mainScreen.setStyle("-fx-background-image: url(\"/bubbles.jpg\");"
				+ "-fx-background-size: 720, 720;-fx-background-repeat: repeat;");
		HBox titlePane = new HBox();
		Text title = new Text("Ethan's Car Wash Simulation");
		title.setFont(mainFont);
		title.setFill(Color.WHITE);
		titlePane.setAlignment(Pos.CENTER);
		titlePane.getChildren().add(title);
		mainScreen.setTop(titlePane);

		VBox inputPane = new VBox();
		Text hoursInput = new Text("\n\t\t\t\tHours to simulate:");
		Text minutesInput = new Text("\t\t\t\tMinutes to simulate:");
		Text carsInput = new Text("\t\t\tAmount of cars waiting at open:");
		TextField hoursField = new TextField();
		TextField minutesField = new TextField();
		TextField carsField = new TextField();

		hoursInput.setFont(textFont);
		minutesInput.setFont(textFont);
		carsInput.setFont(textFont);
		hoursInput.setFill(Color.WHITE);
		minutesInput.setFill(Color.WHITE);
		carsInput.setFill(Color.WHITE);
		inputPane.setSpacing(20);
		inputPane.getChildren().addAll(hoursInput, hoursField, minutesInput, minutesField, carsInput, carsField);
		mainScreen.setCenter(inputPane);

		HBox simulatePane = new HBox();
		Button simulateStart = new Button("Simulate!");
		simulatePane.setAlignment(Pos.CENTER);
		simulatePane.getChildren().add(simulateStart);
		mainScreen.setBottom(simulatePane);

		Scene firstScene = new Scene(mainScreen, 720, 420);
		window.setScene(firstScene);
		window.show();

		simulateStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				hours = Integer.parseInt(hoursField.getText());
				minutes = Integer.parseInt(minutesField.getText());
				numCarsInQueue = Integer.parseInt(carsField.getText());
				totalMins = (hours * 60) + minutes; // Time in minutes
				runSimulator();
				BorderPane resultScreen = new BorderPane();
				window.setTitle("Results");
				resultScreen.setPadding(new Insets(20, 20, 20, 20));
				resultScreen.setStyle("-fx-background-image: url(\"/bubbles.jpg\");"
						+ "-fx-background-size: 720, 720;-fx-background-repeat: repeat;");

				VBox resultsPane = new VBox();
				Text numServed = new Text("\n\t\t\tCustomers Served:\t" + carsWashed);
				numServed.setFont(textFont);
				numServed.setFill(Color.WHITE);
				Text numIdle = new Text("\t\t\tMinutes car wash was idle:\t" + idleTimeTotal);
				numIdle.setFont(textFont);
				numIdle.setFill(Color.WHITE);
				Text avgWait = new Text("\t\t\tAverage wait in minutes:\t" + waitAverage);
				avgWait.setFont(textFont);
				avgWait.setFill(Color.WHITE);
				Text longestWait = new Text("\t\t\tLongest wait in minutes:\t" + longestWaitTime);
				longestWait.setFont(textFont);
				longestWait.setFill(Color.WHITE);
				Text numBypassed = new Text("\t\t\tNumber of customers that bypassed:\t" + carsBypassed);
				numBypassed.setFont(textFont);
				numBypassed.setFill(Color.WHITE);
				resultsPane.setSpacing(35);
				resultsPane.getChildren().addAll(numServed, numIdle, avgWait, longestWait, numBypassed);
				HBox buttonLine = new HBox();
				Button closeButton = new Button("Close");
				buttonLine.setAlignment(Pos.CENTER);
				buttonLine.getChildren().add(closeButton);

				closeButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						window.close();
					}
				});

				resultScreen.setBottom(buttonLine);
				resultScreen.setCenter(resultsPane);
				Scene resultScene = new Scene(resultScreen, 720, 420);

				window.setScene(resultScene);
				window.show();

			}
		});
	}

	public static void runSimulator() {

		int thisIdleTime = 0;

		for (int i = 0; i < numCarsInQueue; i++) {
			tempCar = new Car(0, 0);
			tempCar.serviceLength = (int) ((Math.random() * 4) + 2);

			carsWaiting.add(tempCar);
			lastCar = tempCar;
		}

		if (!carsWaiting.isEmpty()) {
			currentCar = carsWaiting.remove();
			currentCar.departTime = currentCar.serviceLength;

			waitTimes.add(currentCar.waitTime);
			prevCar = currentCar;
		}

		else if (carsWaiting.isEmpty()) {
			lastCar = generateCar();
		}

		for (currentTime = 0; currentTime < totalMins; currentTime++) {

			if (!carsWaiting.isEmpty()) {
				lastCar = carsWaiting.peek();
			}

			else if (carsWaiting.isEmpty() && prevCar.departTime < currentTime) {

				idleTimeTotal++;
			}

			if (currentTime == lastCar.arrivalTime || lastCar == null) {

				newCar = generateCar();
				carsWaiting.add(newCar);

				if (carsWaiting.getSize() > 4) {
					tempCar2 = carsWaiting.peek();
					carsWaiting.removeLast();
					carsBypassed++;
					lastCar.arrivalTime += (tempCar2.arrivalTime - lastCar.arrivalTime);
				}
				lastCar = newCar;
			}

			if (currentTime == currentCar.departTime) {
				prevCar = currentCar;
				carsWashed++;
			}

			if (currentTime == prevCar.departTime) {

				if (!carsWaiting.isEmpty()) {
					currentCar = carsWaiting.remove();

					if (carsWaiting.isEmpty()) {
						thisIdleTime = currentCar.arrivalTime - prevCar.departTime;
						idleTimeTotal += thisIdleTime;
					}

					currentCar.serviceStartTime = prevCar.departTime + thisIdleTime;
					currentCar.waitTime = currentCar.serviceStartTime - currentCar.arrivalTime;
					currentCar.departTime = currentCar.waitTime + currentCar.serviceLength + currentCar.arrivalTime;
					waitTimes.add(currentCar.waitTime);
				}
			}
		}

		waitAverage = findWaitAverage();
		longestWaitTime = findLongestWait();
	}

	public static Car generateCar() {

		arrivalTime = (int) ((Math.random() * 6) + 1);
		serviceTime = (int) ((Math.random() * 4) + 2);
		newCar = new Car(arrivalTime, serviceTime);

		if (lastCar != null) {
			newCar.arrivalTime += lastCar.arrivalTime;
		}
		return newCar;
	}

	public static int findWaitAverage() {

		for (int i = 0; i < waitTimes.size(); i++) {
			waitTotal += waitTimes.get(i);
		}

		waitAverage = waitTotal / waitTimes.size();

		return waitAverage;
	}

	public static int findLongestWait() {
		longestWait = 0;
		for (int w = 0; w < waitTimes.size(); w++) {
			if (waitTimes.get(w) > longestWait) {
				longestWait = waitTimes.get(w);
			}
		}
		return longestWait;
	}
}
