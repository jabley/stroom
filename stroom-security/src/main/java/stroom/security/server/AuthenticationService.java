/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.security.server;

import stroom.security.shared.User;

public interface AuthenticationService {
    /**
     * Login with a Certificate or the session
     *
     * @return
     * @throws RuntimeException
     */
    User autoLogin() throws RuntimeException;

    /**
     * Login with user name / password
     */
    User login(String userName, String password) throws RuntimeException;

    /**
     * Logs the current user out.
     *
     * @return The user name of the user that was logged out.
     */
    String logout() throws RuntimeException;

    /**
     * Returns the current user.
     *
     * @return current user
     */
    User getCurrentUser() throws RuntimeException;

    String getCurrentUserId() throws RuntimeException;

    void refreshCurrentUser() throws RuntimeException;

//    /**
//     * Determines if a user has been authenticated for the current session.
//     *
//     * @return true, if an authenticated user exists
//     */
//    boolean isUserAuthenticated() throws RuntimeException;

    /**
     * Updates an already authenticated users password.
     *
     * @param user
     *            unique user name
     * @param oldPassword
     *            old password
     * @param newPassword
     *            new password
     * @return System user containing modified password
     */
    User changePassword(User user, String oldPassword, String newPassword) throws RuntimeException;

    /**
     * Resets a users password.
     *
     * @param user
     *            unique user name
     * @param password
     *            password
     * @return system user containing modified password
     */
    User resetPassword(User user, String password) throws RuntimeException;

    boolean canEmailPasswordReset();

    /**
     * Resets a users password.
     *
     * @param user
     *            unique user name
     * @return system user containing modified password
     */
    User emailPasswordReset(User user) throws RuntimeException;

    /**
     * Resets a users password.
     *
     * @param userName
     *            unique user name
     * @return system user containing modified password
     */
    Boolean emailPasswordReset(String userName) throws RuntimeException;
}
