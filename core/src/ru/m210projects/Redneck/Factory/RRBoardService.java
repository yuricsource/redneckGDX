package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Board;
import ru.m210projects.Build.BoardService;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.collections.SpriteMap;
import ru.m210projects.Build.Types.collections.ValueSetter;

import java.util.List;

import static ru.m210projects.Build.Engine.*;

public class RRBoardService extends BoardService {

    protected void initSpriteLists(Board board) {
        List<Sprite> sprites = board.getSprites();
        this.spriteStatMap = createSpriteMap(MAXSTATUS, sprites, MAXSPRITES, Sprite::setStatnum);
        this.spriteSectMap = createSpriteMap(MAXSECTORS, sprites, MAXSPRITES, Sprite::setSectnum);
    }

    protected SpriteMap createSpriteMap(int listCount, List<Sprite> spriteList, int spriteCount, ValueSetter<Sprite> valueSetter) {
        return new SpriteMap(listCount, spriteList, spriteCount, valueSetter) {
            @Override
            protected Sprite getInstance() {
                Sprite spr = super.getInstance();
                spr.setStatnum(MAXSTATUS);
                spr.setSectnum(MAXSECTORS);
                return spr;
            }
        };
    }
}
