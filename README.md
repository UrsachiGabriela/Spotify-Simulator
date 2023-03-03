# Spotify-Simulator


Spotify Simulator App based on Service-Oriented-Architecture (SOA) --> REST & SOAP

BACKEND: 

  The application is composed of 3 services: 
    Identity Management -> implemented in python using SOAP,
    Songs_Artists -> implemented in Java using REST,
    Playlists -> implemented in Java using REST.

  These services are secured based on JWT token.
  There is also a Gateway component which customizes the way SOAP operations are exposed as REST resources.


FRONTEND: 

  It is used React.js with class components.
