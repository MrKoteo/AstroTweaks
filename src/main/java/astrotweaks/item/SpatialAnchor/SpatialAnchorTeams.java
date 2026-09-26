package astrotweaks.item.SpatialAnchor;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

/**
 * Оптимизированная работа с team-коллизиями для SpatialAnchor.
 * - Одна команда {@value #TEAM_NAME} с collisionRule NEVER на весь сервер.
 * - Кэш Scoreboard&rarr;Team через WeakHashMap (один lookup вместо createTeam каждый тик).
 * - Сохранение предыдущей команды игрока для корректного восстановления.
 * - Все операции только на сервере (world.isRemote == false).
 */
public final class SpatialAnchorTeams {

    public static final String TEAM_NAME = "nocollis";

    private static final Map<Scoreboard, ScorePlayerTeam> TEAM_CACHE = new WeakHashMap<>();
    private static final Map<UUID, String> PREV_TEAM = new ConcurrentHashMap<>();

    private SpatialAnchorTeams() {}

    private static ScorePlayerTeam getOrCreateTeam(Scoreboard sb) {
        synchronized (TEAM_CACHE) {
            ScorePlayerTeam cached = TEAM_CACHE.get(sb);
            if (cached != null) {
                // проверка что кэш ещё актуален (команда не удалена вручную)
                if (sb.getTeam(TEAM_NAME) == cached) {
                    if (cached.getCollisionRule() != Team.CollisionRule.NEVER) {
                        cached.setCollisionRule(Team.CollisionRule.NEVER);
                    }
                    return cached;
                }
            }
            ScorePlayerTeam team = sb.getTeam(TEAM_NAME);
            if (team == null) {
                team = sb.createTeam(TEAM_NAME);
                team.setCollisionRule(Team.CollisionRule.NEVER);
            } else if (team.getCollisionRule() != Team.CollisionRule.NEVER) {
                team.setCollisionRule(Team.CollisionRule.NEVER);
            }
            TEAM_CACHE.put(sb, team);
            return team;
        }
    }

    public static boolean isInNoCollisionTeam(EntityPlayer player) {
        Scoreboard sb = player.world.getScoreboard();
        ScorePlayerTeam our = sb.getTeam(TEAM_NAME);
        if (our == null) return false;
        ScorePlayerTeam cur = sb.getPlayersTeam(player.getName());
        return cur == our;
    }

    /**
     * Добавить игрока в no_collision команду, сохранив предыдущую.
     * Идемпотентно — если уже в команде, ничего не делает.
     */
    public static void ensureInTeam(EntityPlayer player) {
        if (player.world == null || player.world.isRemote) return;

        Scoreboard sb = player.world.getScoreboard();
        ScorePlayerTeam our = getOrCreateTeam(sb);
        ScorePlayerTeam cur = sb.getPlayersTeam(player.getName());
        if (cur == our) return; // уже в нашей — выходим

        if (cur != null) {
            // сохраняем только первую оригинальную команду, не перезаписываем если уже есть запись
            PREV_TEAM.putIfAbsent(player.getUniqueID(), cur.getName());
        }
        // Scoreboard.addPlayerToTeam автоматически снимет с предыдущей команды
        sb.addPlayerToTeam(player.getName(), our.getName());
    }

    /**
     * Убрать игрока из no_collision команды и вернуть в предыдущую если была.
     * Идемпотентно.
     */
    public static void ensureRemoved(EntityPlayer player) {
        if (player == null || player.world == null || player.world.isRemote) return;
        Scoreboard sb = player.world.getScoreboard();
        ScorePlayerTeam our = sb.getTeam(TEAM_NAME);
        if (our == null) {
            // команды нет — нечего делать, просто чистим кэш prev если вдруг остался
            // не чистим PREV_TEAM здесь, т.к. восстановление уже не нужно
            return;
        }
        ScorePlayerTeam cur = sb.getPlayersTeam(player.getName());
        if (cur != our) return; // не в нашей команде

        // снимаем с нашей
        try {
            sb.removePlayerFromTeam(player.getName(), our);
        } catch (IllegalStateException ignored) {/* ошибки обычно нет - пусто */}

        String prevName = PREV_TEAM.remove(player.getUniqueID());
        if (prevName != null) {
            ScorePlayerTeam prev = sb.getTeam(prevName);
            if (prev != null) {
                sb.addPlayerToTeam(player.getName(), prev.getName());
            }
        }
    }

    /** Очистка памяти при логауте — без восстановления команды если игрок офлайн (вызывается из ensureRemoved). */
    public static void onPlayerLogout(EntityPlayer player) {
        // стараемся восстановить команду даже при выходе, чтобы не оставлять мусор в scoreboard.dat
        ensureRemoved(player);
        // чистим prev если ensureRemoved не сработал (игрок не был в нашей команде)
        PREV_TEAM.remove(player.getUniqueID());
        // инвалидировать кэш нельзя — scoreboard живёт дальше
    }

    /** Для тестов/дебага */
    //public static int cachedTeamCount() {
    //    synchronized (TEAM_CACHE) { return TEAM_CACHE.size(); }
    //}
}
