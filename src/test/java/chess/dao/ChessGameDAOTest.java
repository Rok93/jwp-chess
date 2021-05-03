package chess.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chess.domain.game.ChessGameEntity;
import chess.dto.ChessGameStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@JdbcTest
class ChessGameDAOTest {

    private ChessGameDAO chessGameDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        chessGameDAO = new ChessGameDAO(jdbcTemplate);
    }

    @DisplayName("상태가 BlackTurn 또는 WhiteTurn인 체스 게임을 찾는다")
    @Test
    void testFindByStateIsBlackTurnOrWhiteTurn() {
        //given
        jdbcTemplate.update("INSERT INTO chess_game(state, title) VALUES(?, ?)", new Object[]{"BlackTurn", "title"});

        //when
        ChessGameEntity chessGameEntity = chessGameDAO.findByStateIsBlackTurnOrWhiteTurn().get();

        //then
        assertAll(
                () -> assertThat(chessGameEntity).isNotNull(),
                () -> assertThat(chessGameEntity.getState()).isEqualTo("BlackTurn")
        );
    }

    @DisplayName("새로운 체스게임을 만드는 기능을 테스트한다 ")
    @Test
    void testCreate() {
        //when
        Long createdId = chessGameDAO.save("title");

        //then
        assertThat(createdId).isNotNull();
    }

    @DisplayName("체스게임의 상태를 업데이트 하는 기능을 테스트한다")
    @Test
    void testUpdateState() {
        //given
        Long chessGameId = chessGameDAO.save("title");

        //when
        chessGameDAO.updateState(chessGameId, "WhiteTurn");

        //then
        ChessGameEntity chessGameEntity = chessGameDAO.findById(chessGameId).get();
        assertThat(chessGameEntity).isNotNull();
        assertThat(chessGameEntity.getState()).isEqualTo("WhiteTurn");
    }

    @DisplayName("진행중인 게임이 있을 때, 현재 진행중인 게임이 있는지 찾는다")
    @Test
    void testFindIsExistPlayingChessGameStatusIfExist() {
        //given
        jdbcTemplate.update("INSERT INTO chess_game(state, title) VALUES(?, ?)", new Object[]{"BlackTurn", "title"});

        //when
        ChessGameStatusDto chessGameStatus = chessGameDAO.findIsExistPlayingChessGameStatus();

        //then
        assertThat(chessGameStatus).isNotNull();
        assertThat(chessGameStatus.isExist()).isTrue();
    }

    @DisplayName("진행중인 게임이 없을 때, 현재 진행중인 게임이 있는지 찾는다")
    @Test
    void testFindIsExistPlayingChessGameStatusIfNotExist() {
        //given
        jdbcTemplate.update("INSERT INTO chess_game(state, title) VALUES(?, ?)", new Object[]{"End", "title"});

        //when
        ChessGameStatusDto chessGameStatus = chessGameDAO.findIsExistPlayingChessGameStatus();

        //then
        assertThat(chessGameStatus).isNotNull();
        assertThat(chessGameStatus.isExist()).isFalse();
    }

}
