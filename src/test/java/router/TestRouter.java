package router;

import server.router.Router;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author stuartdd
 */
public class TestRouter {
    @Test
    public void findFileNameRout() {
        Router r = new Router();
        r.add(new String[]{"play", "file", "*"}, new Integer("1"));
        r.add(new String[]{"root2", "..."}, new Integer("2"));
        Assert.assertEquals(new Integer("1"), r.match(new String[]{"play", "file", "1-welcome.wav"}));
    }
    
    @Test
    public void findDotDotDot() {
        Router r = new Router();
        r.add(new String[]{"root1", "sub1", "..."}, new Integer("1"));
        r.add(new String[]{"root2", "..."}, new Integer("2"));
        Assert.assertEquals(new Integer("1"), r.match(new String[]{"root1", "sub1", "sub2"}));
        Assert.assertEquals(new Integer("2"), r.match(new String[]{"root2", "sub1", "sub2"}));
        Assert.assertNull(r.match(new String[]{"root1", "sub2", "z"}));
        Assert.assertNull(r.match(new String[]{"root1", "sub2"}));
        Assert.assertNull(r.match(new String[]{"root1"}));
        Assert.assertNull(r.match(new String[]{"root2"}));
    }


    @Test
    public void find() {
        Router r = new Router();
        r.add(new String[]{"root1", "sub1", "sub2"}, new Integer("1"));
        r.add(new String[]{"root1", "sub1", "sub3"}, new Integer("2"));
        r.add(new String[]{"root1", "sub4"}, new Integer("3"));
        r.add(new String[]{"root2", "sub1", "sub2"}, new Integer("4"));
        r.add(new String[]{"root2", "sub1", "sub3"}, new Integer("5"));
        r.add(new String[]{"root2", "sub4"}, new Integer("6"));
        r.add(new String[]{"root5", "*"}, new Integer("7"));
        r.add(new String[]{"root6", "*", "sub3"}, new Integer("8"));
        r.add(new String[]{"*", "*", "sub9"}, new Integer("9"));
        r.add(new String[]{"single"}, new Integer("10"));

        Assert.assertEquals(new Integer("1"), r.match(new String[]{"root1", "sub1", "sub2"}));
        Assert.assertEquals(new Integer("2"), r.match(new String[]{"root1", "sub1", "sub3"}));
        Assert.assertEquals(new Integer("3"), r.match(new String[]{"root1", "sub4"}));
        Assert.assertEquals(new Integer("4"), r.match(new String[]{"root2", "sub1", "sub2"}));
        Assert.assertEquals(new Integer("5"), r.match(new String[]{"root2", "sub1", "sub3"}));
        Assert.assertEquals(new Integer("6"), r.match(new String[]{"root2", "sub4"}));
        Assert.assertEquals(new Integer("7"), r.match(new String[]{"root5", "any"}));
        Assert.assertEquals(new Integer("8"), r.match(new String[]{"root6", "any", "sub3"}));
        Assert.assertEquals(new Integer("9"), r.match(new String[]{"*", "any2", "sub9"}));
        Assert.assertEquals(new Integer("10"), r.match(new String[]{"single"}));

        Assert.assertNull(r.match(new String[]{"any1", "any2", "sub9"}));
        Assert.assertNull(r.match(new String[]{"root3"}));
        Assert.assertNull(r.match(new String[]{"root2"}));
        Assert.assertNull(r.match(new String[]{"root1"}));

        Assert.assertNull(r.match(new String[]{"any1", "any2", "sub8"}));
        Assert.assertNull(r.match(new String[]{"root6", "any"}));
        Assert.assertNull(r.match(new String[]{"root5", "any", "no"}));
        Assert.assertNull(r.match(new String[]{"root1", "sub4", "x"}));
        Assert.assertNull(r.match(new String[]{"root1", "sub1"}));
        Assert.assertNull(r.match(new String[]{"root1", "x"}));
        Assert.assertNull(r.match(new String[]{"root1", "y", "z"}));
    }

    @Test
    public void testTwo() {
        Router r = new Router();
        r.add(new String[]{"root1", "sub1", "sub2"}, 0);
        r.add(new String[]{"root1", "sub1", "sub3"}, 0);
        r.add(new String[]{"root1", "sub4"}, 0);
        r.add(new String[]{"root2", "sub1", "sub2"}, 0);
        r.add(new String[]{"root2", "sub1", "sub3"}, 0);
        r.add(new String[]{"root2", "sub4"}, 0);
        String s = r.list(new StringBuilder(), 0);
        Assert.assertEquals("0:root1:1:sub1:2:sub2: --> 2:sub3: --> 1:sub4: --> 0:root2:1:sub1:2:sub2: --> 2:sub3: --> 1:sub4: -->", r.list(new StringBuilder(), 0));
    }

    @Test
    public void testSingle() {
        Router r = new Router();
        r.add(new String[]{"name"}, 0);
        Assert.assertEquals("0:name: -->", r.list(new StringBuilder(), 0));
    }

    @Test
    public void testEmpty() {
        Router r = new Router();
        r.add(new String[]{}, 0);
        Assert.assertEquals("", r.list(new StringBuilder(), 0));
    }
}
