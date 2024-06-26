# Flipped Desktop Widget

## Overview

The Flipped Desktop Widget is a versatile desktop utility designed to enhance productivity and provide essential information at a glance. The widget operates in two main views: a front view displaying the current time and weather, and a back view featuring a monthly calendar.


## Features

### Front View
- **Time and Weather Display:** Shows the current time and weather information for a specified location. Users can input a different location to get real-time updates for that area.
- **Customizable Appearance:** Users can personalize the widget's appearance by choosing from various font sizes, colors, and types to fit their desktop aesthetic. Additionally, users have the option to switch between different styles of the menu bar icon to better match their personal preferences or desktop theme.
  
![FrontViewDesktop](https://github.com/NYU-ZYJCS/Flip-Clock/blob/main/img/FrontViewDesktop.png)

![FrontViewDesktopWithOtherApp](https://github.com/NYU-ZYJCS/Flip-Clock/blob/main/img/FrontViewWithOtherApp.PNG)

### Back View
- **Calendar:** Displays a monthly calendar allowing users to quickly view the current date and navigate through months.
  
![BackViewDesktop](https://github.com/NYU-ZYJCS/Flip-Clock/blob/main/img/BackViewDesktop.png)

![BackViewWithOtherApp](https://github.com/NYU-ZYJCS/Flip-Clock/blob/main/img/BackViewWithOtherApp.png)

### User Interactions
- **Spacebar Flip:** Users can flip between the front view (time and weather) and the back view (calendar) by pressing the spacebar.

## Files Description

- **`DesktopWidget.java`:** Main class handling the initialization and display of the widget.
- **`WeatherPrinter.java`:** Manages the retrieval and display of weather information.
- **`TimePrinter.java`:** Handles the display of current time based on the user's specified location.
- **`CalendarPrinter.java`:** Provides the functionality to display a monthly calendar and navigate through dates.
- **`FlipManager.java`:** Handles the flipping between front and back views.
- **`WindowManager.java`:** Handles the window positions for front and back views.

## Setup and Usage

To run the Flipped Desktop Widget, ensure Java is installed on your system and compile the source files. Execute the **`DesktopWidget.java`** file to launch the widget. You can customize settings by clicking on the widget icon in the menu bar:

- **Set Location:** Change the location to receive updated time and weather information.
- **Preferences:** Customize user settings, such as icon style, font type, font size, and font color.
- **Exit:** Click "Exit" to close the widget.
