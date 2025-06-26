package io.peanut.routing;

public class Bootstrap
{
    public static void main(String[] args)
    {
        HttpRouter<NoopJob> httpRouter = HttpRouterFactory.create(config -> {
            config.add("/api/v2/internal/game_request/:game_id", new NoopJob());
            config.add("/api/v2/internal/game_init", new NoopJob());
            config.add("/api/v2/internal/game_init/:ray_id", new NoopJob());
            config.add("/api/v2/internal/game_closed", new NoopJob());
        });

        RouteResult<NoopJob> result = httpRouter.route("/api/v2/internal/game_request/644df7");
        RouteResult<NoopJob> result2 = httpRouter.route("/api/v2/internal/game_init");
        RouteResult<NoopJob> result3 = httpRouter.route("/api/v2/internal/game_init/er");
    }

    private static class NoopJob
    {

    }
}
