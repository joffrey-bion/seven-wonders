# Technical decisions and issues log

## 2019.05.02-07 Frontend migration to TypeScript
[@joffrey-bion][1] — :key: *Frontend, TypeScript*

Flow is nice, but doesn't give me the safety I expect. In its nature, Flow is a type checker aside from the build of 
the project. This means that it is possible to build the project successfully even with type errors in it, depending on how it is 
configured.

I also wasn't too happy about the performance of the IDE integration of Flow. Maybe I didn't put much effort into 
configuring things properly, but I did try multiple settings with more or less safety/performance. The overall 
experience I had was not that great in this respect.

TypeScript came and saved the day. During the migration, it spotted several places that Flow didn't point out and 
where the types were incorrect. Also, I could clean up a bit the types of the redux actions and their creators.

## 2018.07.05-10 Backend migration to Kotlin
[@joffrey-bion][1] — :key: *Backend, Kotlin*

Kotlin really improves on Java on multiple aspects:

- nullability is encoded in the type system, which means no more NPEs and unnecessary null checks
- no unnecessary verbosity: Kotlin is much more condensed than Java for declaring classes and all their members, 
implementing delegation, transforming collections, etc.
- the stdlib has 2 different interfaces for read only and mutable collections
- extension functions allow to add methods to an existing type without extending it
- and many more...

Kotlin is completely interoperable with Java and can be adopted incrementally, so I decided to give it a try.
I migrated the game engine and backend server of Seven Wonders to see how it goes and if I could do this 
kind of change at work. For now, this has been quite a success.

## 2017.08-2018.04 Livedoc development
[@joffrey-bion][1] — :key: *API, Documentation*

Not much has been done on the Seven Wonders project for a while, because I stopped to build 
[Livedoc](https://joffrey-bion.github.io/livedoc/).
I now have a decent and usable documentation generation tool, which requires almost no configuration at all.

It still lacks a couple features:
- Specialized STOMP endpoints presentation (they currently are presented as HTTP endpoints)
- Client generation (especially Flow/TS type defs, bonus for the WebSocket client)

I feel like we're not far from having something very useful now.

## 2017.05.25-28 Flow type-checking + ImmutableJS
[@joffrey-bion][1] — :key: *Frontend*

Javascript can easily become a mess when the code base grows, and static typing helps wrapping one's head around 
what's going on, especially when multiple people are involved in writing code in the same project.

That's why we considered [TypeScript](https://www.typescriptlang.org/) and [Flow](https://flow.org/) for Seven 
wonders. Since I use TypeScript at work already, I had a preference for Flow in order to learn something new, and 
[@victorchabbert][2] had actually this in mind already.
 
Victor introduced Flow in the project and started adding some types already, but this didn't work well with 
seamless-immutable. After reconsideration, it seems that I had missed the `Record` type of ImmutableJS, which allows 
for direct property access like `player.displayName` instead of the `player.get('displayName')` I hated so much. This
removes one of the biggest downsides I felt about ImmutableJS.

Of course, there is still the problem of debugging immutable structures in the console, but I guess we can deal with 
that for now. Here we are, back to Immutable JS.   

## 2017.05.25-27 Web socket integration tests - Jackstomp
[@joffrey-bion][1] — :key: *Backend, Tests*

Unit tests are great, but only check individual components. To build a more robust test suite, we needed to add 
tests that validate:
- the Spring configuration
- STOMP destinations, subscriptions and exchanges
- the serialized data in the messages
- all of the above in the context of a real-life scenario

Using `@SpringBootTest` made it fairly easy to have a server running during unit tests. On top of that, I had to 
configure a web socket client to properly exchange Jackson-serialized objects over STOMP.

Since the configuration of such a client was not as smooth as I expected, I created 
[Jackstomp client](https://github.com/joffrey-bion/jackstomp), which makes it quite straightforward to get a client 
running with sensible defaults. 

## 2017.05.13 Migration to seamless-immutable
[@joffrey-bion][1] — :key: *Frontend, Immutable*

Using Immutable JS has proved to be a pain, especially because the cumbersome API is not contained in the reducers but 
leaks out in the React components. As far as accessing the data is concerned, I dislike not being able to do it the 
native way (`myObj.prop`), well supported by IDEs. What's more, using strings in such accesses (`myObj.get('prop')`) 
is not refactoring-friendly and it obscures the errors when we make typos.

Using `.toJS()` in each selector seemed to be a solution to avoid such accesses in React components, but it in fact 
destroys performance as the new props are never considered the same as the old ones, and therefore the DOM is always 
completely re-rendered.

After reading [Alex Faunt's experience](https://medium.com/@AlexFaunt/immutablejs-worth-the-price-66391b8742d4) and 
[Richard Feldman's article](http://tech.noredink.com/post/107617838018/switching-from-immutablejs-to-seamless-immutable), 
I decided to move to [Seamless Immutable](https://github.com/rtfeldman/seamless-immutable), which was our life savior: 
it provides immutability with an equivalent mutation API, while keeping native read accesses. Of course migrating to 
Seamless Immutable required using [redux-seamless-immutable](https://github.com/eadmundo/redux-seamless-immutable) for 
seamless-immutable-compatible `combineReducers()` and `routerReducer()` functions.

## 2017.04.06 Live API documentation
[@joffrey-bion][1] — :key: *Backend, API, Documentation*

As frontend development was starting, we felt the need for a better API doc than a plain shared Google Sheet.
The best doc is an up-to-date doc that stays so. The easiest way I found to keep it up-to-date is to generate it.

Now multiple tools were available, but none really for websocket APIs documentation. Most of the tools out there are 
specialized in REST APIs. A very popular one is [Swagger](http://swagger.io/), but it 
[can't be used](https://github.com/swagger-api/swagger-socket/issues/47) 
[yet for Websocket](https://github.com/OAI/OpenAPI-Specification/issues/523) communications.

My attention then went to [JsonDoc](http://jsondoc.org/), which looked promising but with the same drawbacks: no 
support for publish/subscribe mechanisms like websockets. Contributing to the project seems easier than contributing 
to Swagger, and I already could tweak a little bit the source code of JsonDoc to support `@MessageMapping` methods.

That being said, Fabio Mafioletti does not seem to have a lot of time available for collaboration in implementing 
this support, so I might have to release from my own fork of the project, or create a new project based on JsonDoc.

## 2017.01.20 Frontend architecture refactoring
[@victorchabbert][2] — :key: Frontend, Redux*

I based the frontend architecture on [mxstbr](https://twitter.com/mxstbr)'s
[react-boilerplate](https://github.com/mxstbr/react-boilerplate) (thanks Max!). The recommended structure for his 
boilerplate is to group files by features. As such, in a feature folder, you would find reducers, actions types and 
creators, selectors, sagas as well as containers.

At first, this choice seemed suitable for our project but with time, I found out that it was causing us headaches 
because of the amount of shared data and logic we have between containers and pages. This is when I remembered 
[Matteo's](https://twitter.com/mazzarolomatteo) blog post on 
[a maintainable project structure](https://hackernoon.com/my-journey-toward-a-maintainable-project-structure-for-react-redux-b05dfd999b5#.o9pn60cv9) from last october. I highly recommend reading.

I refactored our code to use the `Ducks` principle (the word comes from re*DUX*) and I can already see the ease to 
import all redux specific files. I will update this section with more info after using it more extensively.

## 2017.01.20 Unified build Spring Boot + React
[@joffrey-bion][1] — :key: *Build, CI, Deployment*

Some nice plugins allowed for bundling, minification etc. directly from Gradle, but I wanted to make use of the local 
`package.json` scripts to be consistent with the frontend development workflow. 
That way, the global build would not bring any surprise compared to the standalone frontend build.

In order to take advantage of Create React App's genericity, I could not temper with the webpack config in order to 
customize the source path to `src/main/js`, like in the example of 
[Spring React Boilerplate](https://github.com/pugnascotia/spring-react-boilerplate). It eventually made more sense to
separate the react app sources in a subproject, following the example of 
[Geowarin Boot React](https://github.com/geowarin/boot-react/).

Using the [Gradle Node Plugin](https://github.com/srs/gradle-node-plugin) and its Yarn tasks allowed for an easy setup
of the react frontend build. It already solved most of the frontend build problems: download node/npm/yarn, install
dependencies, bundle the JS using the frontend tools... We just had to include the result of the frontend build into the
`static` folder of the backend jar, by customizing the backend's `build.gradle`.

#### Heroku issues

First problem with Heroku: it now tried to run `gradle stage` as it did not recognize a Spring Boot application anymore
in the root folder. I had to manually override the Gradle command in the app settings on Heroku to run a `gradle build`.

Second issue, the `gradle build` command actually runs the tests, which in the frontend subproject starts in watch mode,
because not on a CI server (checking the environment variable `CI=true`). Also, the tests were already run by Travis CI,
so we didn't in fact need them there. The final command for Heroku was `gradle assemble`, which does the job.

Third problem, the default web process for Heroku's dyno looked for jars in the `{projectRoot}/build/lib` folder. I had
to add a Procfile to manually specify the `backend` subproject in the process command:

    web: java -Dserver.port=$PORT $JAVA_OPTS -jar backend/build/libs/*.jar

## 2016.12.08 React frontend
[@joffrey-bion][1] — :key: *Frontend*

We decided to put the react stuff into `src/main/js` as it matches maven/gradle project structure conventions. As 
`src/main/java` contains the Java sources, why not put the JavaScript sources in `src/main/js`?

The technical choices were mostly guided by [@victorchabbert][1], as I had little experience with React, and the 
frontend stuff in general.

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

## 2016.12.04 Spring Boot backend
[@joffrey-bion][1] — :key: *Backend*

Technical decisions:

- [Websockets](https://en.wikipedia.org/wiki/WebSocket): as we needed server pushes to reach all clients when a player
plays a card, we chose WebSockets as main transport. HTTP/2 didn't seem to have sufficient browser support at the moment,
and long polling is not a very clean solution to this problem.
Now, since we have a permanent TCP connection because of the WebSocket, we may as well use it for all actions (even those 
who would naturally follow a request/response mechanism) for simplicity and to avoid HTTP's overhead.

- [Spring Boot Websocket](https://spring.io/guides/gs/messaging-stomp-websocket/): because of my personal preference for
(and ease with) the Java language, and because it is really quick to setup, Spring Boot was our choice as backend. It 
has the advantage to run without a container, with a simple `main()` method which also pretty cool. This gave us the 
quickest start as far as the backend was concerned.

- [STOMP](https://en.wikipedia.org/wiki/Streaming_Text_Oriented_Messaging_Protocol): coming with Spring official
support, it was quite natural to use STOMP over WebSockets as a subprotocol when setting up Spring Boot WebSocket. It 
allows for an easy management of the payload and provides some sort of standard for the publish/subscribe mechanism.

[1]: https://github.com/joffrey-bion
[2]: https://github.com/victorchabbert
