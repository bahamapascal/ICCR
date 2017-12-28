/**
 *
 */
package org.iotacontrolcenter.dto;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
        server.setPort(8234);
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
        return "http://localhost:8234";
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        ActivityDto activity = new ActivityDto();
        activity.add(5);

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

        ActivityDto activity = new ActivityDto();

        ZonedDateTime startDateTime = NeighborDto.currentDateTime()
                .minus(Period.ofDays(1))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        long start = nbr.getTickAtTime(startDateTime);
        long end = nbr.getTickAtTime(NeighborDto.currentDateTime());

        activity.add(start, end);
        nbr.setActivity(activity);
        assertEquals(100, nbr.getActivityPercentageOverLastDay());

        activity.remove(start, end);
        nbr.setActivity(activity);
        assertEquals(0, nbr.getActivityPercentageOverLastDay());
    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#getActivityPercentageOverLastWeek()}.
     */
    @Test
    public final void testActivityPercentageOverLastWeek() {

        // Test 100% activity
        ActivityDto activity = new ActivityDto();
        ZonedDateTime startDateTime = NeighborDto.currentDateTime()
                .minus(Period.ofWeeks(1)).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        long start = nbr.getTickAtTime(startDateTime);
        long end = nbr.getTickAtTime(NeighborDto.currentDateTime());

        activity.add(start, end);
        nbr.setActivity(activity);
        assertEquals(100, nbr.getActivityPercentageOverLastWeek());

        // Test 0% activity
        activity = new ActivityDto();
        activity.remove(start, end);
        nbr.setActivity(activity);
        assertEquals(0, nbr.getActivityPercentageOverLastWeek());

        // Test 50% activity
        long now = nbr.getCurrentTick();
        long weekAgo = nbr.getTickAtTime(
                ZonedDateTime.now(ZoneOffset.UTC).minus(Period.ofWeeks(1)));

        activity = new ActivityDto();
        activity.add((weekAgo + now) / 2, now);
        nbr.setActivity(activity);
        assertEquals(50, nbr.getActivityPercentageOverLastWeek());

    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.NeighborDto#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsObject() {
        ActivityDto activity = new ActivityDto();
        activity.add(5);

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
        assertThat(nbr.getActivity(), instanceOf(ActivityDto.class));
    }

    /**
     * Test method for
     * {@link org.iotacontrolcenter.dto.NeighborDto#getActivityTickLength()},
     * {@link org.iotacontrolcenter.dto.NeighborDto#getActivityGranularity()}
     * and
     * {@link org.iotacontrolcenter.dto.NeighborDto#getActivityRefreshTime()}.
     * 
     */
    @Test
    public final void testGetActivityTickLength() {
        // Test granularity -> number of ticks used
        float granularity = nbr.getActivityGranularity();
        float refreshTime = granularity
                / NeighborDto.ACTIVITY_REFRESH_SAMPLE_RATE;

        float assertDelta = 1 / 60;

        assertEquals("refresh time from granularity", refreshTime,
                nbr.getActivityRefreshTime(), assertDelta);

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
        ActivityDto newActivity = new ActivityDto();
        newActivity.add(1L, 10);
        newActivity.add(12345);

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
