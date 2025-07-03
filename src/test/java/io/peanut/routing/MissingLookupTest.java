package io.peanut.routing;

import org.junit.jupiter.api.*;

import java.util.Collections;

@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class MissingLookupTest
{
    private static HttpRouter<String> httpRouter;

    @BeforeAll
    public static void setup()
    {
        httpRouter = HttpRouterFactory.create(config -> {
            config.add("/home", "handlerHome");
            config.add("/about", "handlerAbout");
            config.add("/contact", "handlerContact");
            config.add("/login", "handlerLogin");
            config.add("/logout", "handlerLogout");
            config.add("/profile", "handlerProfile");
            config.add("/settings", "handlerSettings");
            config.add("/help", "handlerHelp");
            config.add("/terms", "handlerTerms");
            config.add("/privacy", "handlerPrivacy");

            config.add("/api/users", "handlerUsers");
            config.add("/api/orders", "handlerOrders");
            config.add("/api/products", "handlerProducts");
            config.add("/api/cart", "handlerCart");
            config.add("/api/payments", "handlerPayments");
            config.add("/admin/dashboard", "handlerDashboard");
            config.add("/admin/users", "handlerAdminUsers");
            config.add("/admin/settings", "handlerAdminSettings");
            config.add("/blog/posts", "handlerBlogPosts");
            config.add("/blog/categories", "handlerBlogCategories");

            config.add("/api/users/list", "handlerUsersList");
            config.add("/api/users/profile", "handlerUserProfile");
            config.add("/api/orders/new", "handlerNewOrder");
            config.add("/api/orders/history", "handlerOrderHistory");
            config.add("/api/products/list", "handlerProductList");
            config.add("/admin/users/list", "handlerAdminUsersList");
            config.add("/api/users/:user_id/profile", "handlerUserProfile");
            config.add("/admin/settings/security", "handlerSecuritySettings");
            config.add("/blog/posts/2024", "handlerBlogPosts2024");
            config.add("/blog/categories/tech", "handlerTechCategory");
            config.add("/blog/categories/lifestyle", "handlerLifestyleCategory");

            config.add("/api/users/list/active", "handlerActiveUsers");
            config.add("/api/users/list/inactive", "handlerInactiveUsers");
            config.add("/api/orders/history/2024", "handlerOrders2024");
            config.add("/admin/settings/security/password", "handlerPasswordSettings");
            config.add("/admin/settings/security/2fa", "handlerTwoFactorAuth");
            config.add("/blog/posts/2024/july", "handlerJulyPosts");
            config.add("/blog/posts/2024/august", "handlerAugustPosts");
            config.add("/shop/cart/checkout", "handlerCheckout");
            config.add("/shop/orders/track", "handlerOrderTracking");
            config.add("/shop/orders/history", "handlerShopOrderHistory");

            config.add("/api/users/list/active/premium", "handlerPremiumActiveUsers");
            config.add("/api/orders/history/2024/january", "handlerJanuaryOrders");
            config.add("/admin/settings/security/password/reset", "handlerPasswordReset");
            config.add("/blog/posts/2024/july/updates", "handlerJulyUpdates");
            config.add("/shop/orders/history/2024/march", "handlerMarchOrderHistory");
            config.add("/shop/cart/checkout/payment", "handlerPaymentProcessing");
            config.add("/shop/cart/checkout/confirmation", "handlerOrderConfirmation");
            config.add("/support/tickets/open/urgent", "handlerUrgentTickets");
            config.add("/support/tickets/closed", "handlerClosedTickets");
            config.add("/support/tickets/closed/2024", "handlerClosedTickets2024");
        });
    }

    //depth = 1
    @Test
    @Order(1)
    public void test_miss_one_depth_last_unknown()
    {
        RouteResult<String> routeResult = httpRouter.route("/any");

        Assertions.assertNotNull(routeResult);
        Assertions.assertNull(routeResult.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, routeResult.getParameters());
    }

    //depth = 2
    @Test
    @Order(2)
    public void test_miss_two_depth_last_unknown()
    {
        RouteResult<String> routeResult = httpRouter.route("/admin/any");

        Assertions.assertNotNull(routeResult);
        Assertions.assertNull(routeResult.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, routeResult.getParameters());
    }

    //depth = 3
    @Test
    @Order(3)
    public void test_miss_three_depth_last_unknown()
    {
        RouteResult<String> routeResult = httpRouter.route("/admin/settings/any");

        Assertions.assertNotNull(routeResult);
        Assertions.assertNull(routeResult.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, routeResult.getParameters());
    }

    //depth = 4
    @Test
    @Order(4)
    public void test_miss_four_depth_last_unknown()
    {
        RouteResult<String> routeResult = httpRouter.route("/admin/settings/security/any");

        Assertions.assertNotNull(routeResult);
        Assertions.assertNull(routeResult.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, routeResult.getParameters());
    }

    @Test
    @Order(5)
    public void test_miss_four_depth_last_parameterized()
    {
        RouteResult<String> routeResult = httpRouter.route("/api/users/any/profile_mock");

        Assertions.assertNotNull(routeResult);
        Assertions.assertNull(routeResult.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, routeResult.getParameters());
    }

    //depth = 5
    @Test
    @Order(6)
    public void test_miss_five_depth_last_unknown()
    {
        RouteResult<String> routeResult = httpRouter.route("/api/users/list/active/any");

        Assertions.assertNotNull(routeResult);
        Assertions.assertNull(routeResult.getHandler());
        Assertions.assertSame(Collections.EMPTY_MAP, routeResult.getParameters());
    }
}
