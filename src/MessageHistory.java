import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

public class MessageHistory {
	private String fileName_;

	public MessageHistory(String fileName)
	{
		fileName_ = fileName;
	}

	public void addMessage(Message message) throws IOException
	{
		boolean append = true;
		try (PrintWriter fileOut = new PrintWriter(new FileWriter(fileName_, append))) {
			fileOut.println(message);
		}
	}

	public Vector<Message> getHistory() throws IOException
	{
		return getHistory(0);
	}

	public Vector<Message> getHistory(int dayFilter) throws IOException
	{
		Vector<Message> messages = new Vector<Message>();
		File file = new File(fileName_);
		file.createNewFile();

		// calculate the filter
		Date filter = null;
		if (dayFilter != 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -dayFilter);
			filter = calendar.getTime();
		}

		try (BufferedReader fileIn = new BufferedReader(new FileReader(fileName_))) {
			StringTokenizer tokenizer;
			for (String m; (m = fileIn.readLine()) != null;) {
				tokenizer = new StringTokenizer(m, "\\;");
				Message message = new Message();
				message.setSenderUsername(tokenizer.nextToken());
				message.setSenderAddress(tokenizer.nextToken());
				String dateString = tokenizer.nextToken();
				Date date = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss").parse(dateString);
				if (filter != null && date.before(filter)) {
					continue;
				}
				message.setDate(date);
				message.setPayload(tokenizer.nextToken());

				messages.addElement(message);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return messages;
	}
}
