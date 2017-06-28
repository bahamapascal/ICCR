/**
 *
 */
package org.iotacontrolcenter.dto;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.BitSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author David Landry <david@dmwl.net>
 *
 */
public class NeighborDtoTest {

    @Path("/hello-world")
    public static class HelloWorldResource {
        @GET
        public String helloWorld() {
            return "Hello World";
        }
    }

    @Path("/get-nbr")
    public static class TestNeighborResource {
        static NeighborDto nbrResource;

        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public NeighborDto getNeighborDto(){
            return nbrResource;
        }
    }

    public static TestNeighborResource endpoint = new TestNeighborResource();
    public static TJWSEmbeddedJaxrsServer server;


    /**
     * Setup embedded web server to invoke REST endpoints
     *
     * @throws Exception
     */

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(8123);
        server.setBindAddress("localhost");
        server.start();
        server.getDeployment().getRegistry().addPerRequestResource(TestNeighborResource.class);
        server.getDeployment().getRegistry().addPerRequestResource(HelloWorldResource.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        server.stop();
    }

    NeighborDto nbr;

    public String baseUri() {
        return "http://localhost:8123";
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        BitSet activity = new BitSet();
        activity.set(5);

        nbr = new NeighborDto(
                "key",
                "uri",
                "name",
                "description",
                true,
                activity,
                10
                );

        nbr.setNumAt(10);
        nbr.setNumIt(10);
        nbr.setNumNt(10);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getActivityPercentageOverLastDay()}.
     */
    @Test
    public final void testActivityPercentageOverLastDay() {
        int activityLength = nbr.getActivityTickLength();

        BitSet activity = new BitSet();
        activity.set(0, activityLength-1);
        nbr.setActivity(activity);
        assertEquals(100, nbr.getActivityPercentageOverLastDay());

        activity.set(0, activityLength-1, false);
        nbr.setActivity(activity);
        assertEquals(0, nbr.getActivityPercentageOverLastDay());

        // Test if refresh time is set to zero
        boolean caughtArgumentException = true;
        try {
            nbr.setIotaNeighborRefreshTime(0);
            caughtArgumentException = false;
        }
        catch (IllegalArgumentException e) {
            caughtArgumentException &= true;
        }

        try {
            nbr.setIotaNeighborRefreshTime(-1);
            caughtArgumentException = false;
        }
        catch (IllegalArgumentException e) {
            caughtArgumentException &= true;
        }

        try {
            nbr.setIotaNeighborRefreshTime(Integer.MIN_VALUE);
            caughtArgumentException = false;
        }
        catch (IllegalArgumentException e) {
            caughtArgumentException &= true;
        }
        assertTrue("NeighhborDto shouldn't allow invalid refresh times", caughtArgumentException);
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getActivityPercentageOverLastWeek()}.
     */
    @Test
    public final void testActivityPercentageOverLastWeek() {
        int activityLength = nbr.getActivityTickLength();

        // Test 100% activity
        BitSet activity = new BitSet();
        activity.set(0, activityLength-1, true);
        nbr.setActivity(activity);
        assertEquals(100, nbr.getActivityPercentageOverLastWeek());

        // Test 0% activity
        activity = new BitSet();
        activity.set(0, activityLength-1, false);
        nbr.setActivity(activity);
        assertEquals(0, nbr.getActivityPercentageOverLastWeek());

        // Test 50% activity
        int now    = nbr.getCurrentTick();
        int weekAgo = nbr.getTickAtTime(
                ZonedDateTime.now(ZoneOffset.UTC).minus(Period.ofWeeks(1)));

        activity = new BitSet(activityLength);
        activity.set((weekAgo+now)/2, now);
        nbr.setActivity(activity);
        assertEquals(50, nbr.getActivityPercentageOverLastWeek());

    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsObject() {
        BitSet activity = new BitSet();
        activity.set(5);

        NeighborDto expected = new NeighborDto(
                "key",
                "uri",
                "name",
                "description",
                true,
                activity,
                10
                );

        expected.setNumAt(10);
        expected.setNumIt(10);
        expected.setNumNt(10);

        assertEquals(expected, nbr);
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getActivity()}.
     */
    @Test
    public final void testGetActivity() {
        assertThat(nbr.getActivity(), instanceOf(BitSet.class));
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getActivityTickLength()}.
     */
    @Test
    public final void testGetActivityTickLength() {
        Duration activityLengthRealTime = Duration.ofDays(14);

        int refreshTime = 5;
        int expected = (int) (activityLengthRealTime.toMinutes()/refreshTime);

        nbr.setIotaNeighborRefreshTime(refreshTime);

        assertEquals(expected, nbr.getActivityTickLength());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getDescr()}.
     */
    @Test
    public final void testGetDescr() {
        assertEquals("description", nbr.getDescr());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getIotaNeighborRefreshTime()}.
     */
    @Test
    public final void testGetIotaNeighborRefreshTime() {
        assertEquals(10, nbr.getIotaNeighborRefreshTime());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getKey()}.
     */
    @Test
    public final void testGetKey() {
        assertEquals("key", nbr.getKey());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getName()}.
     */
    @Test
    public final void testGetName() {
        assertEquals("name", nbr.getName());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getNumAt()}.
     */
    @Test
    public final void testGetNumAt() {
        assertEquals(10, nbr.getNumAt());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getNumIt()}.
     */
    @Test
    public final void testGetNumIt() {
        assertEquals(10, nbr.getNumIt());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getNumNt()}.
     */
    @Test
    public final void testGetNumNt() {
        assertEquals(10, nbr.getNumNt());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getUri()}.
     */
    @Test
    public final void testGetUri() {
        assertEquals("uri", nbr.getUri());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#isActive()}.
     */
    @Test
    public final void testIsActive() {
        assertTrue(nbr.isActive());
    }

    /**
     * Test JSON serialization
     */
    @Test
    public final void testJsonSerialization() {
        TestNeighborResource.nbrResource = nbr;

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(baseUri() + "/get-nbr");

        Response response = target.request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        NeighborDto actual = response.readEntity(NeighborDto.class);
        assertEquals(nbr, actual);

    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setActive(boolean)}.
     */
    @Test
    public final void testSetActive() {
        nbr.setActive(false);
        assertTrue(!nbr.isActive());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setActivity(java.util.BitSet)}.
     */
    @Test
    public final void testSetActivity() {
        BitSet newActivity = new BitSet();
        newActivity.set(1,10);
        newActivity.set(12345);

        nbr.setActivity(newActivity);
        assertEquals(newActivity.hashCode(), nbr.getActivity().hashCode());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setDescr(java.lang.String)}.
     */
    @Test
    public final void testSetDescr() {
        String expected = "test string";
        nbr.setDescr(expected);

        assertEquals(expected, nbr.getDescr());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setIotaNeighborRefreshTime(int)}.
     */
    @Test
    public final void testSetIotaNeighborRefreshTime() {
        int expected = 1;
        nbr.setIotaNeighborRefreshTime(expected);

        assertEquals(expected, nbr.getIotaNeighborRefreshTime());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setKey(java.lang.String)}.
     */
    @Test
    public final void testSetKey() {
        String expected = "test string";
        nbr.setKey(expected);

        assertEquals(expected, nbr.getKey());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setName(java.lang.String)}.
     */
    @Test
    public final void testSetName() {
        String expected = "test string";
        nbr.setName(expected);

        assertEquals(expected, nbr.getName());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setNumAt(int)}.
     */
    @Test
    public final void testSetNumAt() {
        int expected = 1;
        nbr.setNumAt(expected);

        assertEquals(expected, nbr.getNumAt());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setNumIt(int)}.
     */
    @Test
    public final void testSetNumIt() {
        int expected = 1;
        nbr.setNumIt(expected);

        assertEquals(expected, nbr.getNumIt());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setNumNt(int)}.
     */
    @Test
    public final void testSetNumNt() {
        int expected = 1;
        nbr.setNumNt(expected);

        assertEquals(expected, nbr.getNumNt());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#setUri(java.lang.String)}.
     */
    @Test
    public final void testSetUri() {
        String expected = "test string";
        nbr.setUri(expected);

        assertEquals(expected, nbr.getUri());
    }

    /**
     * Test TJWS
     */
    @Test
    public final void testTjws() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(baseUri() + "/hello-world");

        Response response = target.request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String actual = response.readEntity(String.class);
        assertEquals("Hello World", actual);

    }
    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#toString()}.
     */
    @Test
    public final void testToString() {
        String expected = "NeighborDto{" +
                "key='key', name='name', descr='description', active='true', uri='uri'}";
        assertEquals(expected, nbr.toString());
    }

}
