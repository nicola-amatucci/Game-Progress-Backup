package it.nicola_amatucci.android.game_progress_backup.scripts;

import java.util.ArrayList;

public class Command
{
	public String command = null;
	public ArrayList<String> args = new ArrayList<String>();

	public void argsReplaceToken(String token, String replacement)
	{
		for (String arg: args)
			arg.replace(token, replacement);
	}
}
