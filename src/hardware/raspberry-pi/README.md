The IoT Starterkit supports the Raspberry Pi with peripherals attached via a
GrovePi shield and programmed in Python.  In the example we use a Slide
Potentiometer for the input of values that can be changed by the user as well
as an LED and an OLED graphical display for visible output.

![Starterkit running](../../../images/starterkit_running_01.jpg?raw=true "Starterkit running")

Please follow these steps to get the example up and running:
* Install a Raspbian Linux distribution following the instructions and downloadable images from https://www.raspberrypi.org
* Configure the Raspberry Pi to have appropriate network connectivity (we support operation both without and with an HTTP proxy) and make sure your installation is up-to-date using the ```apt-get update; apt-get upgrade``` mechanism
* Install urllib3 with ```sudo apt-get install python-urllib3```
* Install and test the GrovePi software as described at http://www.dexterindustries.com/GrovePi/get-started-with-the-grovepi/
* Go to the GrovePi/Software/Python directory and do a ```sudo python setup.py install``` to install the GrovePi modules for all users of the system
* Copy the file necessary for OLED support to the appropriate installation place with ```sudo cp grove_oled/grove_oled.py /usr/local/lib/python2.7/dist-packages/grovepi-0.0.0-py2.7.egg``` 
* Connect the Slide Potentiometer to A0, the LED to D4 and the OLED display to I2C-1
* Install the files [iot_starterkit_pi_and_grove_peripherals.py](../../examples/python/iot-starterkit-for-pi-and-grove-peripherals/iot_starterkit_pi_and_grove_peripherals.py) as well as [config.py](../../examples/python/iot-starterkit-for-pi-and-grove-peripherals/template-config.py) and configure for your HCP Trial account, credentials, Device and Message Types in [config.py](../../examples/python/iot-starterkit-for-pi-and-grove-peripherals/template-config.py)
* Start the program as root (```sudo bash``` lets you get root in case you have not been already) with ```python iot_starterkit_pi_and_grove_peripherals.py```
* Interact with the respective control and consumption mechanisms as described for desktop usage already
* You can stop the running program with ```Ctrl+C```
