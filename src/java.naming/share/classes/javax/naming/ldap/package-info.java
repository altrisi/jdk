/*
 * Copyright (c) 1999, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 *
 * Provides support for LDAPv3 extended operations and controls.
 *
 * <p>
 * This package extends the directory operations of the Java Naming and
 * Directory Interface (JNDI). &nbsp;
 * JNDI provides naming and directory functionality to applications
 * written in the Java programming language. It is designed to be
 * independent of any specific naming or directory service
 * implementation. Thus a variety of services--new, emerging, and
 * already deployed ones--can be accessed in a common way.
 *
 * <p>
 * This package is for applications and service providers that deal with
 * LDAPv3 extended operations and controls, as defined by
 * <a href=http://www.ietf.org/rfc/rfc2251.txt>RFC 2251</a>.
 * The core interface in this package is {@code LdapContext}, which defines
 * methods on a context for performing extended operations and handling
 * controls.
 *
 * <h2>Extended Operations</h2>
 * <p>
 * This package defines the interface {@code ExtendedRequest}
 * to represent the argument to an extended operation,
 * and the interface {@code ExtendedResponse} to represent the result
 * of the extended operation.
 * An extended response is always paired with an extended request
 * but not necessarily vice versa. That is, you can have an extended request
 * that has no corresponding extended response.
 * <p>
 * An application typically does not deal directly with these interfaces.
 * Instead, it deals with classes that <em>implement</em> these
 * interfaces.
 * The application gets these classes either as part of a
 * repertoire of extended operations standardized through the IETF, or
 * from directory vendors for vendor-specific extended operations.
 * The request classes should have constructors that accept
 * arguments in a type-safe and user-friendly manner, while the
 * response classes should have access methods for getting the data
 * of the response in a type-safe and user-friendly manner.
 * Internally, the request/response classes deal with encoding and decoding
 * BER values.
 * <p>
 * For example, suppose an LDAP server supports a "get time" extended operation.
 * It would supply classes such as
 * {@code GetTimeRequest} and {@code GetTimeResponse},
 * so that applications can use this feature.
 * An application would use these classes as follows:
 * {@snippet :
 * GetTimeResponse resp =
 *     (GetTimeResponse) ectx.extendedOperation(new GetTimeRequest());
 * long time = resp.getTime();
 * }
 * <p>
 * The {@code GetTimeRequest} and {@code GetTimeResponse} classes might
 * be defined as follows:
 * {@snippet :
 * public class GetTimeRequest implements ExtendedRequest {
 *     // User-friendly constructor
 *     public GetTimeRequest() {
 *     };
 *
 *     // Methods used by service providers
 *     public String getID() {
 *         return GETTIME_REQ_OID;
 *     }
 *     public byte[] getEncodedValue() {
 *         return null;  // no value needed for get time request
 *     }
 *     public ExtendedResponse createExtendedResponse(
 *         String id, byte[] berValue, int offset, int length) throws NamingException {
 *         return new GetTimeResponse(id, berValue, offset, length);
 *     }
 * }
 * public class GetTimeResponse implements ExtendedResponse {
 *     long time;
 *     // called by GetTimeRequest.createExtendedResponse()
 *     public GetTimeResponse(String id, byte[] berValue, int offset, int length)
 *         throws NamingException {
 *         // check validity of id
 *         long time =  ... // decode berValue to get time
 *     }
 *
 *     // Type-safe and User-friendly methods
 *     public java.util.Date getDate() { return new java.util.Date(time); }
 *     public long getTime() { return time; }
 *
 *     // Low level methods
 *     public byte[] getEncodedValue() {
 *         return // berValue saved;
 *     }
 *     public String getID() {
 *         return GETTIME_RESP_OID;
 *     }
 * }
 * }
 *
 * <h2>Controls</h2>
 *
 * This package defines the interface {@code Control} to represent an LDAPv3
 * control. It can be a control that is sent to an LDAP server
 * (<em>request control</em>) or a control returned by an LDAP server
 * (<em>response control</em>). Unlike extended requests and responses,
 * there is not necessarily any pairing between request controls and
 * response controls. You can send request controls and expect no
 * response controls back, or receive response controls without sending
 * any request controls.
 * <p>
 * An application typically does not deal directly with this interface.
 * Instead, it deals with classes that <em>implement</em> this interface.
 * The application gets control classes either as part of a repertoire of
 * controls standardized through the IETF, or from directory vendors for
 * vendor-specific controls. The request control classes should have
 * constructors that accept arguments in a type-safe and user-friendly
 * manner, while the response control classes should have access methods
 * for getting the data of the response in a type-safe and user-friendly
 * manner. Internally, the request/response control classes deal with
 * encoding and decoding BER values.
 * <p>
 * For example, suppose an LDAP server supports a "signed results"
 * request control, which when sent with a request, asks the
 * server to digitally sign the results of an operation.
 * It would supply a class {@code SignedResultsControl} so that applications
 * can use this feature.
 * An application would use this class as follows:
 * {@snippet :
 * Control[] reqCtls = new Control[] {new SignedResultsControl(Control.CRITICAL)};
 * ectx.setRequestControls(reqCtls);
 * NamingEnumeration enum = ectx.search(...);
 * }
 * The {@code SignedResultsControl} class might be defined as follows:
 * {@snippet :
 * public class SignedResultsControl implements Control {
 *     // User-friendly constructor
 *     public SignedResultsControl(boolean criticality) {
 *  // assemble the components of the request control
 *     };
 *
 *     // Methods used by service providers
 *     public String getID() {
 *         return // control's object identifier
 *     }
 *     public byte[] getEncodedValue() {
 *         return // ASN.1 BER encoded control value
 *     }
 *     ...
 * }
 * }
 * <p>
 * When a service provider receives response controls, it uses
 * the {@code ControlFactory} class to produce specific classes
 * that implement the {@code Control} interface.
 * <p>
 * An LDAP server can send back response controls with an LDAP operation
 * and also with enumeration results, such as those returned
 * by a list or search operation.
 * The {@code LdapContext} provides a method ({@code getResponseControls()})
 * for getting the response controls sent with an LDAP operation,
 * while the {@code HasControls} interface is used to retrieve
 * response controls associated with enumeration results.
 * <p>
 * For example, suppose an LDAP server sends back a "change ID" control in response
 * to a successful modification. It would supply a class {@code ChangeIDControl}
 * so that the application can use this feature.
 * An application would perform an update, and then try to get the change ID.
 * {@snippet :
 * // Perform update
 * Context ctx = ectx.createSubsubcontext("cn=newobj");
 *
 * // Get response controls
 * Control[] respCtls = ectx.getResponseControls();
 * if (respCtls != null) {
 *     // Find the one we want
 *     for (int i = 0; i < respCtls.length; i++) {
 *         if(respCtls[i] instanceof ChangeIDControl) {
 *      ChangeIDControl cctl = (ChangeIDControl)respCtls[i];
 *      System.out.println(cctl.getChangeID());
 *         }
 *     }
 * }
 * }
 * The vendor might supply the following {@code ChangeIDControl} and
 * {@code VendorXControlFactory} classes. The {@code VendorXControlFactory}
 * will be used by the service provider when the provider receives response
 * controls from the LDAP server.
 * {@snippet :
 * public class ChangeIDControl implements Control {
 *     long id;
 *
 *     // Constructor used by ControlFactory
 *     public ChangeIDControl(String OID, byte[] berVal) throws NamingException {
 *         // check validity of OID
 *         id = // extract change ID from berVal
 *     };
 *
 *     // Type-safe and User-friendly method
 *     public long getChangeID() {
 *         return id;
 *     }
 *
 *     // Low-level methods
 *     public String getID() {
 *         return CHANGEID_OID;
 *     }
 *     public byte[] getEncodedValue() {
 *         return // original berVal
 *     }
 *     ...
 * }
 * public class VendorXControlFactory extends ControlFactory {
 *     public VendorXControlFactory () {
 *     }
 *
 *     public Control getControlInstance(Control orig) throws NamingException {
 *         if (isOneOfMyControls(orig.getID())) {
 *      ...
 *
 *      // determine which of ours it is and call its constructor
 *      return (new ChangeIDControl(orig.getID(), orig.getEncodedValue()));
 *  }
 *         return null;  // not one of ours
 *     }
 * }
 * }
 *
 * <h2>Package Specification</h2>
 *
 * The JNDI API Specification and related documents can be found in the
 * {@extLink jndi_overview JNDI documentation}.
 *
 * @since 1.3
 */
package javax.naming.ldap;
