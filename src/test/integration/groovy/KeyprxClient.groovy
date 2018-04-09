

// NOT FINISHED

import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder

import static groovyx.net.http.ContentTypes.JSON

class KeyprxClient {

    private final HttpBuilder httpJSON

    KeyprxClient(final String host) {
        httpJSON = HttpBuilder.configure {
            request.uri = host ?: 'http://localhost:8080'
            request.contentType = JSON[0]
        }
    }

    Map registerBlock(String name, int capacity, int overbookPercent) {
        def requestBody = [
                "name"           : name,
                "capacity"       : capacity,
                "overbookPercent": overbookPercent
        ]
        Object postResponse = httpJSON.post(Object) {
            request.uri.path = '/api/blocks'
            request.body = requestBody
            response.exception { Exception t ->
                t.printStackTrace()
                throw new RuntimeException(t)
            }
            response.success { FromServer resp, def body ->
                body
            }
        }
        if (!postResponse instanceof Map) {
            throw new RuntimeException("Invalid response type")
        }
        postResponse as Map
    }


    Collection fetchBlocks() {
        Object postResponse = httpJSON.get(Object) {
            request.uri.path = '/api/blocks'
            response.exception { Exception t ->
                t.printStackTrace()
                throw new RuntimeException(t)
            }
            response.success { FromServer resp, def body ->
                body
            }
        }
        if (!postResponse instanceof Map) {
            throw new RuntimeException("Invalid response type")
        }
        postResponse as Collection
    }

    /*


        String accessTokenIn = null
        String thirdPartyTokenIn = null
        def requestBody = [
                "device": deviceProfileMap
        ]
        def loginResponse = httpJSON.post(Object) {
            request.uri.path = '/gatekeeper/user/guest/login'
            request.body = requestBody
            response.exception { Exception t ->
                t.printStackTrace()
                throw new RuntimeException(t)
            }
            response.success { FromServer resp, def body ->
                if (!body.success) {
                    throw new Error("Request was not successful")
                }
                accessTokenIn = FromServer.Header.find(
                        resp.headers, 'X-ACCESS-TOKEN'
                )?.value
                thirdPartyTokenIn = FromServer.Header.find(
                        resp.headers, 'X-ACCESS-TOKEN-THIRDPARTY'
                )?.value
                body
            }
        }
        PlayerAuth auth = new PlayerAuth(deviceProfileMap, loginResponse.data.id as String, accessTokenIn, thirdPartyTokenIn)
        PlayerState state = new PlayerState(loginResponse.data)
        return [auth, state]
     */

}