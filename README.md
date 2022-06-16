# Trip Planner

## Overview
### Description
User can use the app to plan the trip and generate a route to visit the attractions of their interest. The app generates a list of attractions in this city. Then user is able to select the attractions they want to visit. After clicking create, close to most effective route is created for the user to visit all of attractions they picked.

App will utilize TripAdvisors and Google Maps API to get the attractions and their locations in the city, and then display a map with markers marking the attractions and highlighted route that connects the markers. Trip Planner will also implement the algorithm that will try to come up with the close to most efficient solution. App will also use Parse to store the user's username, password, profile picture, past trips, etc.

Additional Features:
1. Adding friends to your trip. If friends are going on a trip together, when creating a new trip, user is able to  look up and add their friends to the trip. When the user selects attractions and clicks create, app will combine the attractions list with user's friends attraction lists (if a few users want to see Eiffel Tower, combined list will include it) and create a finalized most efficient route.
2. After generating the route, app will identify which attraction contributes to the total trip time most and will let the user now about it, suggesting a potential removal of this attraction from the list. User can remove the attraction, from the route page if they decide to do so.


### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Travel
- **Mobile:** This app would be primarily developed for mobile. Functionality wouldn’t be limited to mobile devices, however mobile version could potentially have more features.
- **Story:** Based on the country user wants to visist, app generates list of cities in this country. After user picks cities, app generates the list of attractions in all of the picked cities. App displays the most efficient route that allows user to see all of the picked attractions. If some attraction is a bit farther than others it will let the user know that there are not a lot of other attractions nearby this particular one that is far and suggests removing this attraction from the list. User can remove if they want to.
- **Market:** Any individual who is planning to travel and visit new places can use the app.
- **Habit:** This app can be used every time user is planning to travel and wants to see how their journey can look like.
- **Scope:**I want to start with one country, and then I will expand the scope to continent and more countriew.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User authenticates to see their previous searches
* User signs up, if no account
* Profile Page for the user
* User picks country, cities, attractions
* App generates route

**Optional Nice-to-have Stories**

* App identifies the farthest attraction that takes most time and suggests removing it to the user
* User can add their friends to trip so that app generates a combined list of attractions
* Users get matched to other users that are planning to visit the same countries/places
* User can share their route with particular user

### 2. Screen Archetypes

* Login
    * Register screen - User signs up or logs into their account if no account
* Profile screen
* New Trip Screen
* Pick attractions screen
* Route screen

### 3. Animations
* Attractions list item slightly expands when hovered over
* Use Material Design to add visual polish
* Double tap on marker to remove it from the route map / double tap on attraction to see more information


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* New Trip
* Profile

**Flow Navigation** (Screen to Screen)

* Log-in -> Account creation if no log in is available
* Profile -> Text field to be modified.
* New Trip -> cities selection -> attractions selection -> suggested route
*
## Wireframes
Figma Link
https://www.figma.com/file/xztwsd8viegSP3aYomUqyV/Trip-Planner?node-id=0%3A1
