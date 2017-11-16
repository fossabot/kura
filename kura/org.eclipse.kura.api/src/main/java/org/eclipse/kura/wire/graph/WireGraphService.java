package org.eclipse.kura.wire.graph;

import java.util.List;

import org.eclipse.kura.configuration.metatype.OCD;

public interface WireGraphService {

    void update(WireGraphConfiguration graphConfiguration);

    void delete();

    WireGraphConfiguration get();

    // should this go to another service? Maybe the ConfigurationService should provide an API
    List<OCD> findWireComponentDescriptions();
}
