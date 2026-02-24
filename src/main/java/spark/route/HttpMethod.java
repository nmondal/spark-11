/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.route;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author Per Wendel
 */
public enum HttpMethod {
    get, post, put, patch, delete, head, trace, connect, options, before, after, afterafter, unsupported;

    // all possible routes of optimizing it completely failed
    // this is the fastest way to cast and reverse cast
    // https://medium.com/javarevisited/micro-optimizations-in-java-good-nice-and-slow-enum-261e6f77bd2e
    private static HashMap<String, HttpMethod> methods = new HashMap<>();

    static {
        // https://stackoverflow.com/questions/5258977/are-http-headers-case-sensitive
        for (HttpMethod method : values()) {
            final String lowerCased = method.toString();
            final String upperCased = lowerCased.toUpperCase(Locale.ROOT);
            methods.put(lowerCased, method);
            methods.put(upperCased, method);
        }
    }

    /**
     * Gets the HttpMethod corresponding to the provided string. If no corresponding method can be found
     * {@link spark.route.HttpMethod#unsupported} will be returned.
     *
     * @param methodStr The string containing HTTP method name
     * @return          The HttpMethod corresponding to the provided string
     */
    public static HttpMethod get(String methodStr) {
        return methods.getOrDefault(methodStr, unsupported );
    }
}
