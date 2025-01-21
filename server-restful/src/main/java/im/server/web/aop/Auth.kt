package im.server.web.aop

import common.beans.Role


annotation class Auth(val role: Role = Role.User)
