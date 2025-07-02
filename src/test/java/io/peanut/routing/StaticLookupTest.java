package io.peanut.routing;

import org.junit.jupiter.api.*;

import java.util.Collections;

@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class StaticLookupTest
{
    private static HttpRouter<String> httpRouter;

    @BeforeAll
    public static void setup()
    {
        httpRouter = HttpRouterFactory.create(config -> {
            config.add("/home", "handlerHome");
            config.add("/about", "handlerAbout");
            config.add("/contact", "handlerContact");

            config.add("/api/v1/users", "handlerUsers");
            config.add("/api/v1/users/list", "handlerUsersList");
            config.add("/api/v1/users/profile", "handlerUserProfile");

            config.add("/api/v1/orders", "handlerOrders");
            config.add("/api/v1/orders/new", "handlerNewOrder");
            config.add("/api/v1/orders/history", "handlerOrderHistory");

            config.add("/admin", "handlerAdmin");
            config.add("/admin/dashboard", "handlerDashboard");
            config.add("/admin/users", "handlerAdminUsers");
            config.add("/admin/settings", "handlerSettings");

            config.add("/products", "handlerProducts");
            config.add("/products/list", "handlerProductList");
            config.add("/products/view", "handlerProductView");
            config.add("/products/reviews", "handlerProductReviews");
            config.add("/products/reviews/latest", "handlerLatestReviews");

            config.add("/blog", "handlerBlog");
            config.add("/blog/posts", "handlerBlogPosts");
            config.add("/blog/posts/2024", "handlerBlogPosts2024");
            config.add("/blog/posts/2024/july", "handlerBlogJuly");
            config.add("/blog/posts/2024/july/updates", "handlerBlogUpdates");

            config.add("/shop", "handlerShop");
            config.add("/shop/cart", "handlerCart");
            config.add("/shop/cart/checkout", "handlerCheckout");
            config.add("/shop/orders", "handlerShopOrders");
            config.add("/shop/orders/track", "handlerOrderTrack");

            config.add("/user", "handlerUser");
            config.add("/user/settings", "handlerUserSettings");
            config.add("/user/settings/privacy", "handlerPrivacy");
            config.add("/user/settings/notifications", "handlerNotifications");

            config.add("/support", "handlerSupport");
            config.add("/support/faq", "handlerFaq");
            config.add("/support/contact", "handlerSupportContact");
            config.add("/support/tickets", "handlerTickets");
            config.add("/support/tickets/open", "handlerOpenTickets");

            config.add("/news", "handlerNews");
            config.add("/news/local", "handlerLocalNews");
            config.add("/news/international", "handlerInternationalNews");
            config.add("/news/sports", "handlerSportsNews");
            config.add("/news/technology", "handlerTechNews");

            config.add("/events", "handlerEvents");
            config.add("/events/conferences", "handlerConferences");
            config.add("/events/webinars", "handlerWebinars");
            config.add("/events/workshops", "handlerWorkshops");
            config.add("/events/workshops/online", "handlerOnlineWorkshops");
        });
    }

    // depth = 1
    @Test
    @Order(1)
    public void test_hit_one_depth_leading_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/home");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerHome", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(2)
    public void test_hit_one_depth()
    {
        RouteResult<String> result = httpRouter.route("/home");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerHome", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(3)
    public void test_hit_one_depth_trailing_delimiter()
    {
        RouteResult<String> result = httpRouter.route("home/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerHome", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(4)
    public void test_hit_one_depth_all_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/home/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerHome", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }



    // depth = 2
    @Test
    @Order(5)
    public void test_hit_two_depth_leading_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/admin/settings");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerSettings", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(6)
    public void test_hit_two_depth()
    {
        RouteResult<String> result = httpRouter.route("admin/settings");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerSettings", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(7)
    public void test_hit_two_depth_trailing_delimiter()
    {
        RouteResult<String> result = httpRouter.route("admin/settings/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerSettings", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(8)
    public void test_hit_two_depth_all_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/admin/settings/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerSettings", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }



    // depth = 3
    @Test
    @Order(9)
    public void test_hit_three_depth_leading_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/events/workshops/online");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerOnlineWorkshops", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(10)
    public void test_hit_three_depth()
    {
        RouteResult<String> result = httpRouter.route("events/workshops/online");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerOnlineWorkshops", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(11)
    public void test_hit_three_depth_trailing_delimiter()
    {
        RouteResult<String> result = httpRouter.route("events/workshops/online/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerOnlineWorkshops", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(12)
    public void test_hit_three_depth_all_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/events/workshops/online/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerOnlineWorkshops", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }



    // depth = 4
    @Test
    @Order(13)
    public void test_hit_four_depth_leading_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/api/v1/users/list");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerUsersList", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(14)
    public void test_hit_four_depth()
    {
        RouteResult<String> result = httpRouter.route("api/v1/users/list");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerUsersList", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(15)
    public void test_hit_four_depth_trailing_delimiter()
    {
        RouteResult<String> result = httpRouter.route("api/v1/users/list/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerUsersList", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(16)
    public void test_hit_four_depth_all_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/api/v1/users/list/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerUsersList", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }



    //depth = 5
    @Test
    @Order(17)
    public void test_hit_five_depth_leading_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/blog/posts/2024/july/updates");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerBlogUpdates", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(18)
    public void test_hit_five_depth()
    {
        RouteResult<String> result = httpRouter.route("blog/posts/2024/july/updates");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerBlogUpdates", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(19)
    public void test_hit_five_depth_trailing_delimiter()
    {
        RouteResult<String> result = httpRouter.route("blog/posts/2024/july/updates/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerBlogUpdates", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }

    @Test
    @Order(20)
    public void test_hit_five_depth_all_delimiter()
    {
        RouteResult<String> result = httpRouter.route("/blog/posts/2024/july/updates/");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("handlerBlogUpdates", result.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, result.getParameters());
    }
}
