# Technical decisions and issues log

## 2017-01-20 Frontend architecture refactoring
:key: : Frontend

I based the frontend architecture on [mxstbr's](https://twitter.com/mxstbr) [react-boilerplate](https://github.com/mxstbr/react-boilerplate) (thanks Max!). The recommended structure for his boilerplate is to group files by features. As such, in a feature folder, you would find reducers, actions types and creators, selectors, sagas as well as containers.

At first, this choice seemed suitable for our project but with time, I found out that it was causing us headaches because of the amount of shared data and logic we have between containers and pages. This is when I remembered [Matteo's](https://twitter.com/mazzarolomatteo) blog post on [a maintainable project structure](https://hackernoon.com/my-journey-toward-a-maintainable-project-structure-for-react-redux-b05dfd999b5#.o9pn60cv9) from last october. I highly recommend reading.

I refactored our code to use the `Ducks` principle (the word comes from re*DUX*) and I can already see the ease to import all redux specific files.
I will update this section with more info after using it more extensively.

## 2017-01-20 Unified build Spring Boot + React
:key: : Frontend, Backend


Using the initial `src/main/js` was troublesome, because it didn't really follow any standard, and we would need a local
`build.gradle` to handle the frontend in order to be clean. It eventually made more sense to separate the react app
sources in a subproject, following the example of [Geowarin Boot React](https://github.com/geowarin/boot-react/).

I wanted to make use of the local `package.json` scripts to be consistent with the development workflow and take
advantage of Create React App's genericity. Some other nice plugins allowed for bundling, minification etc. directly
from Gradle, but that's not exactly what I wanted.

Using the [Gradle Node Plugin](https://github.com/srs/gradle-node-plugin) and its Yarn tasks allowed for an easy setup
of the react frontend build. It already solved most of the frontend build problems: download node/npm/yarn, install
dependencies, bundle the JS using the frontend tools... We just had to include the result of the frontend build into the
`static` folder of the backend jar.

#### Heroku issues

First problem with Heroku: it now tried to run `gradle stage` as it did not recognize a Spring Boot application anymore
in the root folder. I had to manually override the Gradle command in the app settings on Heroku to run a `gradle build`.

Second issue, the `gradle build` command actually runs the tests, which in the frontend subproject starts in watch mode,
because not on a CI server (checking the environment variable `CI=true`). Also, the tests were already run by Travis CI,
so we did't in fact need them there. The final command for Heroku was `gradle assemble`, which does the job.

Third problem, the default web process for Heroku's dyno looked for jars in the `{projectRoot}/build/lib` folder. I had
to add a Procfile to manually specify the `backend` subproject in the process command:

    web: java -Dserver.port=$PORT $JAVA_OPTS -jar backend/build/libs/*.jar

## 2016-12-08 React frontend
:key: : Frontend

We decided to put the react stuff into `src/main/js` as it seems to follow the conventions. As `src/main/java` contains
the Java sources, why not put the JavaScript sources in `src/main/js`?

Technical decisions:

- [Create React App](https://github.com/facebookincubator/create-react-app): because the front-end world evolves too
fast to keep up with the tools!
- [Yarn](https://yarnpkg.com/) : for its performance and reliability compared to npm
- [Redux](http://redux.js.org/): because it seems to solve the state management problem really well, and sagas look
promising
- [Immutable.js](https://facebook.github.io/immutable-js/): because Redux likes pure functions, and Immutable.js does a
great job at enforcing it

So far, no special build including the produced static inside the webapp's Jar. Frontend and backend can be developed
independently and both the frontend dev server and the spring boot server can run locally and communicate.

## 2016-12-04 Spring Boot backend
:key: : Backend

Technical decisions:

- [Websockets](https://en.wikipedia.org/wiki/WebSocket): as we want server pushes to reach all clients when a player
plays a card, we chose Websockets as main transport.

- [Spring Boot Websocket](https://spring.io/guides/gs/messaging-stomp-websocket/): because of my personal preference for
and ease with the Java language, and because it is really quick to setup. It has the advantage to run without a
container, with a simple `main()` method which also pretty cool.

- [STOMP](https://en.wikipedia.org/wiki/Streaming_Text_Oriented_Messaging_Protocol): coming with Spring official
support, it was quite natural to use STOMP over Websockets as a subprotocol. It allows for an easier management of the
payload and provide some sort of standard for the Publish/Subscribe mechanism.
