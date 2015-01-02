package at.cpickl.grotlin.multi

import javax.ws.rs.core.Response
import at.cpickl.grotlin.endpoints.Fault


/**
 * User has provided some bad input data (resulting in a 400 BadRequest response).
 */
class UserException(message: String, fault: Fault) : FaultException(message, Response.Status.BAD_REQUEST, fault)

/**
 * User requested a non-existing resource (resulting in a 404 FileNotFound response).
 */
class NotFoundException(message: String, fault: Fault) : FaultException(message, Response.Status.NOT_FOUND, fault)
