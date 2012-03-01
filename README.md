OpenWifiStatistics
==================

OpenWifiStatistics is an Android application used to scan wifi networks, 
gather statistics about them (including location) and upload the data to a
server for analysis.

License
-------

    OpenWifiStatistics is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

Disclaimer
----------

This application stores sensitive information(bssid, ssid, geolocation data) 
in order to demonstrate an easy and cheap way of wifi Access points scanning. 
The data are publicly available at http://uberspot.ath.cx/wifi/results.php. 
The purpose of logging them is only for producing statistics relative to 
the most popular wifi settings. By any mean they will not be used to indentify 
real persons or for any other commercial use.

Client and Server
-----------------

By the term "client" we refer to the Android application that scans for APs 
(Access Points). With the term "server" we refer to the PHP scripts that 
automatically produce the statistics.

Client
------

The client does the three very simple things:

 * Scans for APs
 * Provides the current location by gps or network triangulation
 * When connected to the internet uploads the data to the server
 
The wifi scans, the data uploading and the location updates happens every x seconds. 
The interval time depends on the user speed. There are three available settings/usage
scenarios. Car mode (aggressive scanning), on foot mode (less frequent scanning), stationary
(scans once every 30 minutes). Also there is an option to set your own interval times.
The whole purpose of these settings is to reduce power consumption when aggressive 
scanning is not necessary.

In more detail the wifi scans include information such as the BSSID and the
SSID, the access point capabilities (aka security settings), the frequency
that the access point operates and the signal's power.

The additional information that we log is the timestamp when the scan
took place, the location coordinates and the location provider as described
previously (available values are gps or network).

Gps tends to be more accurate in external places but is more power hungry.
Network provider is less power consuming and accurate but in enclosed locations
it's a good alternative if the gps can't get a reading.
We use them both to get better findings.

Server
------

The server gets the results via http POST made by the client. The server
setup consists of some PHP scripts and a MySQL database.

The web interface consists of 3 pages, the results page, the map page and
the statistics page.

The results are all the wifi scans, ordered by date, security type or power.

The map is a google map with clusters of wifis, so we can easily get an idea
about how many wifis exist in a specific area.

Last, the statistics page automatically estimates and displays various statistics.

I am going to mention these results in the next section.

Finally, both map and stats page are cached every 15 minutes for the pages
to load faster.

Statistics
----------

We have made more than 11k scans and have scanned more then 5k different
access points (APs). You can see the stats live here: http://uberspot.ath.cx/wifi/stats.php.

About the frequency, the 4 most popular and dominant frequencies are the
2437(CH8), the 2412(CH1), the 2462(CH12) and the 2452(CH8).

Another measurement that we are making is the most popular AP vendors. For
achieving that we had to use the manufacturer's list shipped with wireshark
and match them with the AP's MAC address prefixes (a.k.a. BSSID).

The 3 more common vendors are Thomson Telecom Belgium, BaudTec Corporation
and Intracom S.A.

Then, we have some security measurements. In general most people use
WPA variants for their wlan security. However, for our disappointment
WEP was the 3rd most popular security setting with 774 AP using it.

Our measurements showed that approximately 5.9% of the whole wifi's that
we have recorded were completely unprotected.

More about Security
-------------------

Our findings show us that people are generally ignorant about wlan security.

Specifically:

Thomson AP's WEP/WPA algorithm vulnerability (2008 models, how many are still there?):
http://www.gnucitizen.org/blog/default-key-algorithm-in-thomson-and-bt-home-hub-routers/
http://www.kaisersblog.com/2008/07/hacking-thompson-speedtouch-routers-with-default-security-settings-crack-wepwpa-keys-within-minutes/

WEP is a broken protocol:
http://en.wikipedia.org/wiki/Cracking_of_wireless_networks#Wired_Equivalent_Privacy_.28WEP.29
http://www.wi-fiplanet.com/tutorials/article.php/1368661

WPS has vulnerabilities:
http://blog.thesysadmins.co.uk/wifi-protected-setup-wps-vulnerability.html

IBSS a.k.a. ad hoc networks can be dangerous:
http://www.airtightnetworks.com/home/resources/knowledge-center/viral-ssid.html

Open networks may be dangerous. Of course, in order to provide public access a
network has to be open. Such kind of networks are for example the AWMN access points.
However, we have certainly spotted some cases of unnecessary use of open networks.

Completion of the Project Objectives
------------------------------------

We certainly proved that network mapping, recording and extraction
of statistical results has been made easy and cheap through embedded devices
and mobile computing platforms such as smartphones.

Google is also doing it 
( http://news.cnet.com/8301-31921_3-57324766-281/removing-your-wi-fi-network-from-googles-map/).
Even worse one could expand an application like that to log unencrypted data
( http://news.cnet.com/8301-30684_3-20007277-265.html ).

The idea of crowd sourcing in general can lower the costs of such a task.
Instead of having some expensive units to make a task like that, we could use
many low cost units. In our case we could for example provide our application
to a delivery company employees. In this way we speed up the whole process
with a lower cost.

Project Expansion
-----------------

Now that the main code base exists we can expand it with new features.

For example we could easily query the existing database for patterns between
the time and signal strength, or to find how much populated are the channels
by a specific location.

Acknowledgments
---------------

For the charts we used the Javascript Library AwesomeChartJS.

For minifying the output code with jsmin.php.

For the clusters we used markerclusterer.js
