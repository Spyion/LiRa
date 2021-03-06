package gameStates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.Panel;
import gui.Picture;
import gui.Text;
import levels.Level;
import levels.LevelProgress;
import settings.Graphic;
import tools.BidirectionalMap;
import tools.CSV;
import tools.Loader;
import util.ButtonAction;

public class CustomLevels extends BasicState {

	Panel p;
	List<Object> toRemoveTexts = new ArrayList<>();
	Text selectedText;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

		create(p = new Panel(

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().menu, Color.black, new Vector2f(0, 250))

		));
		super.init(gc, sbg);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		loadLevels(game);
		super.enter(container, game);
	}

	private static Image done = Loader.loadImage("!Menu/done"), undone = Loader.loadImage("!Menu/undone");

	private void loadLevels(StateBasedGame sbg) {

		p.remove(toRemoveTexts);
		toRemoveTexts.clear();

		File file = new File("levels");
		File[] files = file.listFiles();
		int i = 1;
		BidirectionalMap<Level, Text> levelMap = new BidirectionalMap<Level, Text>();
		List<Picture> pictureList = new ArrayList<>();
		for (File file2 : files) {

			Text text = new Text(new ButtonAction() {

				@Override
				public void onRelease(Object source) {
					if (selectedText != null)
						selectedText.setFontColor(Color.black);
					selectedText = (Text) source;
					selectedText.setFontColor(Color.blue);
				}

				@Override
				public void onDoublePress(Object source) {
					States.game.setLevel(levelMap.getKey((Text) source));
					States.game.clear();
					StateHandler.enterState(sbg, Game.class);
				}

				@Override
				public void onDeselect(Object source) {
					if (selectedText != null)
						selectedText.setFontColor(Color.black);
					selectedText = null;
				}

			}, file2.getName().split("\\.")[0], Color.black, new Vector2f(0, i * 40 - Graphic.height / 2 + 20f));
			Level l;
			levelMap.put(l = new Level(new CSV(file2)), text);
			Image image = LevelProgress.isLevelDone(l) ? done : undone;
			Picture p;
			pictureList.add(p = new Picture(new Vector2f(200, i * 40 - Graphic.height / 2 + 20f), 0.3f, image));
			toRemoveTexts.add(text);
			toRemoveTexts.add(p);
			i++;
		}
		p.add(levelMap.getValueSet());
		p.add(pictureList);

	}

}
