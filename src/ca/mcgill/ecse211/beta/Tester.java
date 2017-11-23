package ca.mcgill.ecse211.beta;

import java.util.Map;

import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryDisplay;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.ColorController;
import ca.mcgill.ecse211.sensor.Poller;
import ca.mcgill.ecse211.wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Tester {

	private static final String SERVER_IP = "192.168.43.193";
	private static final int TEAM_NUMBER = 7;
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = false;

	private enum State {
		WIFI, InitialLocalization, NavigateZip, NavigateRiver, TraverseZip, TraverseRiver, BlockSearch, ReturnZip, ReturnRiver
	};

	private static State state = State.WIFI;

	private static int X, Y;
	private static int buttonChoice, startCorner = 0, ZO_R_x = 0, ZO_R_y = 0, ZC_R_x = 0, ZC_R_y = 0, ZO_G_x = 0,
			ZO_G_y = 0, ZC_G_x = 0, ZC_G_y = 0, SH_LL_x = 0, SH_LL_y = 0, SH_UR_x = 0, SH_UR_y = 0, SV_LL_x = 0,
			SV_LL_y = 0, SV_UR_x = 0, SV_UR_y = 0, redTeamNo = 0, greenTeamNo = 0;

	private static void changeState(State currentState) {
		switch (currentState) {
		case WIFI:
			state = State.InitialLocalization;
			break;
		case InitialLocalization:
			if (greenTeamNo == TEAM_NUMBER) {
				state = State.NavigateZip;
			} else {
				state = State.NavigateRiver;
			}
			break;
		case NavigateZip:
			state = State.TraverseZip;
			break;

		case NavigateRiver:
			state = State.TraverseRiver;
			break;
		}
	}

	private static void initializeOdometer(int startCorner, Odometer odometer) {
		if (startCorner == 0) {
			odometer.setX(1 * 30.48);
			odometer.setY(1 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(0);
			Sound.beepSequence();
		} else if (startCorner == 1) {
			odometer.setX(7 * 30.48);
			odometer.setY(1 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(0);
			Sound.beepSequence();
		} else if (startCorner == 2) {
			odometer.setX(7 * 30.48);
			odometer.setY(7 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(180);
			Sound.beepSequence();
		} else if (startCorner == 3) {
			odometer.setX(1 * 30.48);
			odometer.setY(7 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(180);
			Sound.beepSequence();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Resources resources = new Resources("A", "D", "B", "S4", "S3");
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = Resources.getOdometer();
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		Poller poller = new Poller(Resources.getUltrasonicController(), Resources.getColorController());
		ColorController color = new ColorController();

		WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

		odometer.start();
		odometryDisplay.start();
		poller.start();

		try {
			Map data = conn.getData();
			redTeamNo = ((Long) data.get("RedTeam")).intValue();
			greenTeamNo = ((Long) data.get("GreenTeam")).intValue();

			ZO_R_x = ((Long) data.get("ZO_R_x")).intValue();
			ZO_R_y = ((Long) data.get("ZO_R_y")).intValue();
			ZC_R_x = ((Long) data.get("ZC_R_x")).intValue();
			ZC_R_y = ((Long) data.get("ZC_R_y")).intValue();
			startCorner = ((Long) data.get("RedCorner")).intValue();
			ZO_G_x = ((Long) data.get("ZO_G_x")).intValue();
			ZO_G_y = ((Long) data.get("ZO_G_y")).intValue();
			ZC_G_x = ((Long) data.get("ZC_G_x")).intValue();
			ZC_G_y = ((Long) data.get("ZC_G_y")).intValue();
			if (greenTeamNo == 7) {
				startCorner = ((Long) data.get("GreenCorner")).intValue();
			} else {
				startCorner = ((Long) data.get("RedCorner")).intValue();
			}
			
		} catch (Exception e) {
			// System.err.println("Error: " + e.getMessage());
		}
		t.clear();
		Navigation.turnTo(90, false);

		/*
		while (true) {
			switch (state) {
			case WIFI:
				changeState(state);

			case InitialLocalization:
				UltrasonicLocalizer.doLocalization(startCorner);
				initializeOdometer(startCorner, odometer);
				changeState(state);

			case NavigateZip:
				if (greenTeamNo == 7) {
					Navigation.travelTo(ZO_G_x, ZO_G_y);
					LightLocalizer.doLocalization(ZO_G_x, ZO_G_y);
					Navigation.travelTo(ZC_G_x, ZC_G_y);
					changeState(state);
				} else {
					Navigation.travelTo(ZO_R_x, ZO_R_y);
				}
				changeState(state);
				break;

			case NavigateRiver:
				Navigation.travelTo(SH_LL_x, SH_LL_y);
				changeState(state);

			case TraverseZip:
				Navigation.driveZipline();
			}

			while (Button.waitForAnyPress() != Button.ID_ESCAPE)
				;
			System.exit(0);
		}
		*/

	}
}
