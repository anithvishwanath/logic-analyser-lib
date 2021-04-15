package logicanalyser.languages;

import java.util.List;

import com.google.common.net.MediaType;

import logicanalyser.Marker;

public interface MetricContext {
	MediaType getLanguageAt(int index);
	MediaType getLanguageAtLine(int line);
	
	String getContent();
	
	String[] getLines();
	
	int getLineCount();
	
	List<Marker> getMarkers();
}
