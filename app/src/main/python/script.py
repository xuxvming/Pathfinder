# -*- coding: utf-8 -*-
"""
Created on Thu Apr  9 19:41:52 2020

@author: Family
"""

import networkx as nx
import geojson
from math import cos, asin, sqrt
from heapq import heappush, heappop



mode_map = {"walk": ("primary_link", "secondary_link", "tertiary_link","primary", "secondary", "tertiary", "unclassified", "residential", "living_street", "service",
                     "pedestrian", "track", "road", "footway", "steps", "bridleway", "corridor", "path", "both", "left", "right", "crossing", "elevator",
                     "emergency_access_point", "milestone", "mini_roundabout", "passing_place", "street_lamp", "stop", "traffic_mirror", "traffic_signals", "trailhead", "turning_circle"),
            "drive": ("motorway", "motorway_link", "trunk_link", "trunk", "primary_link", "secondary_link", "tertiary_link", "primary", "secondary", "tertiary",
                      "unclassified", "residential", "living_street", "service", "track", "escape", "raceway", "road", "give_way", "milestone", "mini_roundabout",
                      "motorway_junction", "passing_place", "rest_area", "speed_camera", "street_lamp", "stop", "traffic_mirror", "traffic_signals", "turning_circle", "toll_gantry", "turning_loop" ),
            "cycle": ("cycleway", "primary_link", "secondary_link", "tertiary_link", "primary", "secondary", "tertiary",
                      "unclassified", "residential", "living_street", "service", "road", "lane", "opposite", "opposite_lane", "track", "opposite_track",
                      "share_busway", "opposite_share_busway", "shared_lane", "give_way", "milestone", "passing_place", "street_lamp", "stop", "traffic_mirror", "traffic_signals", "turning_circle"),
            "luas": ("Red", "Green"),
            "bus": ("lane", "bus_guideway", "opposite_lane", "bus_stop", "platform", "street_lamp", "stop", "traffic_mirror", "traffic_signals", "turning_circle", "toll_gantry", "turning_loop" ),
            "dart": ("route")}



def valid_edge(edge_dict, mode):
    if "walk" in mode:
        if "highway" in edge_dict:
            if edge_dict["highway"] in mode_map["walk"]:
                return True
        if "sidewalk" in edge_dict:
            if edge_dict["sidewalk"] in mode_map["walk"]:
                return True
    elif "drive" in mode:
        if "highway" in edge_dict:
            if edge_dict["highway"] in mode_map["drive"]:
                return True
    elif "cycle" in mode:
        if "highway" in edge_dict:
            if edge_dict["highway"] in mode_map["cycle"]:
                return True
        if "cycleway" in edge_dict:
            if edge_dict["cycleway"] in mode_map["cycle"]:
                return True
    elif "luas" in mode:
        if "luas" in edge_dict:
            return True
    elif "bus" in mode:
        if "bus" in edge_dict:
            #if edge_dict["highway"] in mode_map["bus"] or edge_dict["busway"] in mode_map["bus"]:
            return True
    elif "dart" in mode:
        if "dart" in edge_dict:
            return True
    return False

def _weight_function(G, weight):
    """Returns a function that returns the weight of an edge.

    The returned function is specifically suitable for input to
    functions :func:`_dijkstra` and :func:`_bellman_ford_relaxation`.

    Parameters
    ----------
    G : NetworkX graph.

    weight : string or function
        If it is callable, `weight` itself is returned. If it is a string,
        it is assumed to be the name of the edge attribute that represents
        the weight of an edge. In that case, a function is returned that
        gets the edge weight according to the specified edge attribute.

    Returns
    -------
    function
        This function returns a callable that accepts exactly three inputs:
        a node, an node adjacent to the first one, and the edge attribute
        dictionary for the eedge joining those nodes. That function returns
        a number representing the weight of an edge.

    If `G` is a multigraph, and `weight` is not callable, the
    minimum edge weight over all parallel edges is returned. If any edge
    does not have an attribute with key `weight`, it is assumed to
    have weight one.

    """
    if callable(weight):
        return weight
    # If the weight keyword argument is not callable, we assume it is a
    # string representing the edge attribute containing the weight of
    # the edge.
    if G.is_multigraph():
        x = lambda u, v, d: min(attr.get(weight, 1) for attr in d.values())
        return x
    return lambda u, v, data: data.get(weight, 1)

def _dijkstra_multisource(G, sources, weight, pred=None, paths=None,
                          cutoff=None, target=None, mode='drive'):
    """Uses Dijkstra's algorithm to find shortest weighted paths

    Parameters
    ----------
    G : NetworkX graph

    sources : non-empty iterable of nodes
        Starting nodes for paths. If this is just an iterable containing
        a single node, then all paths computed by this function will
        start from that node. If there are two or more nodes in this
        iterable, the computed paths may begin from any one of the start
        nodes.

    weight: function
        Function with (u, v, data) input that returns that edges weight

    pred: dict of lists, optional(default=None)
        dict to store a list of predecessors keyed by that node
        If None, predecessors are not stored.

    paths: dict, optional (default=None)
        dict to store the path list from source to each node, keyed by node.
        If None, paths are not stored.

    target : node label, optional
        Ending node for path. Search is halted when target is found.

    cutoff : integer or float, optional
        Depth to stop the search. Only return paths with length <= cutoff.

    Returns
    -------
    distance : dictionary
        A mapping from node to shortest distance to that node from one
        of the source nodes.

    Raises
    ------
    NodeNotFound
        If any of `sources` is not in `G`.

    Notes
    -----
    The optional predecessor and path dictionaries can be accessed by
    the caller through the original pred and paths objects passed
    as arguments. No need to explicitly return pred or paths.

    """
    G_succ = G._succ if G.is_directed() else G._adj

    push = heappush
    pop = heappop
    dist = {}  # dictionary of final distances
    seen = {}
    # fringe is heapq with 3-tuples (distance,c,node)
    # use the count c to avoid comparing nodes (may not be able to)
    from itertools import count
    #pdb.set_trace()
    c = count()
    fringe = []
    for source in sources:
        if source not in G:
            raise nx.NodeNotFound("Source {} not in G".format(source))
        seen[source] = 0
        push(fringe, (0, next(c), source))
    while fringe:
        (d, _, v) = pop(fringe)
        if v in dist:
            continue  # already searched this node.
        dist[v] = d
        if v == target:
            break
        for u, e in G_succ[v].items():
            #pdb.set_trace()
            if not valid_edge(e[0], mode):
                continue
            #import pdb;pdb.set_trace()
            cost = weight(v, u, e)
            if cost is None:
                continue
            vu_dist = dist[v] + cost
            if cutoff is not None:
                if vu_dist > cutoff:
                    continue
            if u in dist:
                if vu_dist < dist[u]:
                    raise ValueError('Contradictory paths found:',
                                     'negative weights?')
            elif u not in seen or vu_dist < seen[u]:
                seen[u] = vu_dist
                push(fringe, (vu_dist, next(c), u))
                if paths is not None:
                    paths[u] = paths[v] + [u]
                if pred is not None:
                    pred[u] = [v]
            elif vu_dist == seen[u]:
                if pred is not None:
                    pred[u].append(v)

    # The optional predecessor and path dictionaries can be accessed
    # by the caller via the pred and paths objects passed as arguments.
    return dist


def shortest_path(G, sources=None, target=None, cutoff=None, weight=None, mode='drive'):
    #print("{}, {}".format(sources, target))
    if not sources:
        raise ValueError('source must not be empty')
    if target in sources:
        return (0, [target])
    weight = _weight_function(G, weight)
    paths = {source: [source] for source in sources}  # dictionary of paths
    dist = _dijkstra_multisource(G, sources, weight, paths=paths,
                                 cutoff=cutoff, target=target, mode=mode)
    if target is None:
        return (dist, paths)
    try:
        #pdb.set_trace()
        return (dist[target], paths[target])
    except KeyError:
        #pdb.set_trace()
        raise nx.NetworkXNoPath("No path to {}.".format(target))


def distance(lat1, lon1, lat2, lon2):
    p = 0.017453292519943295
    a = 0.5 - cos((lat2-lat1)*p)/2 + cos(lat1*p)*cos(lat2*p) * (1-cos((lon2-lon1)*p)) / 2
    m = 12742 * asin(sqrt(a)) * 1000
    return m

def check_if_mode_possible_from_node(graph, check_node, mode):
    #pdb.set_trace()
    for node, edge in graph._succ[check_node].items():
        if not valid_edge(edge[0], mode):
            continue
        else:
            return True
    return False

def closest_node_to_co_ordinate(graph, co_ordinate, mode):
    count = 0
    for node in graph.nodes.data():
        if not check_if_mode_possible_from_node(graph, node[0], mode):
            continue
        if count == 0:
            shortest = distance( co_ordinate[0], co_ordinate[1], node[1]['y'], node[1]['x'] )
            closest = node
            count = 1
        else:
            temp = distance( co_ordinate[0], co_ordinate[1], node[1]['y'], node[1]['x'] )
            if temp < shortest:
                shortest = temp
                closest = node
    return closest[0], shortest

def closest_n_nodes_to_co_ordinate(graph, co_ordinate, n, mode):
    count = 0
    nodes = []
    for node in graph.nodes.data():
        if not check_if_mode_possible_from_node(graph, node[0], mode):
            continue
        if count == 0:
            shortest = distance( co_ordinate[0], co_ordinate[1], node[1]['y'], node[1]['x'] )
            nodes.append(node[0])
            count = 1
        else:
            temp = distance( co_ordinate[0], co_ordinate[1], node[1]['y'], node[1]['x'] )
            if temp < shortest:
                shortest = temp
                nodes.append(node[0])
                if len(nodes) > n:
                    del nodes[0]
    nodes.reverse()
    return nodes

def start_here(start, end, central_mode, other_modes, case, G):
    paths = dict()
    paths['start'] = []
    paths['end'] = []
    start_point = closest_node_to_co_ordinate(G, start, 'walk')[0]
    end_point = closest_node_to_co_ordinate(G, end, 'walk')[0]
    #paths['central'] = [(start_workflow(start, end, central_mode), central_mode)]
    central_path = start_workflow(start, end, central_mode, G)
    for mode in other_modes:
        path = rest_workflow(start, (central_path['start_node']['y'], central_path['start_node']['x']), mode, G)
        paths['start'].append({'length': path[0], 'route': path[1], 'mode': mode})
    for mode in other_modes:
        path = rest_workflow((central_path['end_node']['y'], central_path['end_node']['x']), end, mode, G)
        paths['end'].append({'length': path[0], 'route': path[1], 'mode': mode})
    path = shortest_path(G, sources={start_point}, target=closest_node_to_co_ordinate(G, (central_path['start_node']['y'], central_path['start_node']['x']), 'walk')[0], cutoff=None, weight='length', mode='walk')
    paths['start'].append({'length': path[0], 'route': path[1], 'mode': 'walk'})
    path = shortest_path(G, sources={closest_node_to_co_ordinate(G, (central_path['end_node']['y'], central_path['end_node']['x']), 'walk')[0]}, target=end_point, cutoff=None, weight='length', mode='walk')
    paths['end'].append({'length': path[0], 'route': path[1], 'mode': 'walk'})

    optimum_route = choose_optimum_route(central_path, paths, case)

    return optimum_route


def start_workflow(start, end, central_mode, G):
    end_mode_node = closest_node_to_co_ordinate(G, end, central_mode)[0]
    if central_mode == 'bus':
        paths = []
        start_mode_nodes = closest_n_nodes_to_co_ordinate(G, start, 20, 'bus')
        for node in start_mode_nodes:
            path = shortest_path(G, sources={node}, target=end_mode_node, cutoff=None, weight='length', mode=central_mode)
            if len(path[1]) > 1:
                paths.append(path)
        lengths = []
        for path in paths:
            lengths.append(path[0])
        min_path = lengths.index(min(lengths))
        return {'start_node': G.nodes.data()[start_mode_nodes[min_path]], 'end_node': G.nodes.data()[end_mode_node], 'length': paths[min_path][0], 'route': paths[min_path][1], 'mode': central_mode}
    else:
        start_mode_node = closest_node_to_co_ordinate(G, start, central_mode)[0]
        end_mode_node = closest_node_to_co_ordinate(G, end, central_mode)[0]
        path = shortest_path(G, sources={start_mode_node}, target=end_mode_node, cutoff=None, weight='length', mode=central_mode)
        return {'start_node': G.nodes.data()[start_mode_node], 'end_node': G.nodes.data()[end_mode_node], 'length': path[0], 'route': path[1], 'mode': central_mode}

def rest_workflow(start, end, central_mode, G):
    start_point = closest_node_to_co_ordinate(G, start, 'walk')[0]
    end_point = closest_node_to_co_ordinate(G, end, 'walk')[0]

    start_mode_node = closest_node_to_co_ordinate(G, start, central_mode)[0]
    end_mode_node = closest_node_to_co_ordinate(G, end, central_mode)[0]
    mode_path = shortest_path(G, sources={start_mode_node}, target=end_mode_node, cutoff=None, weight='length', mode=central_mode)

    start_mode_coord = (G.nodes.data()[start_mode_node]['y'], G.nodes.data()[start_mode_node]['x'])
    end_mode_coord = (G.nodes.data()[end_mode_node]['y'], G.nodes.data()[end_mode_node]['x'])

    start_walk_path = shortest_path(G, sources={start_point}, target=closest_node_to_co_ordinate(G, start_mode_coord, 'walk')[0], cutoff=None, weight='length', mode='walk')
    end_walk_path = shortest_path(G, sources={closest_node_to_co_ordinate(G, end_mode_coord, 'walk')[0]}, target=end_point, cutoff=None, weight='length', mode='walk')

    return [start_walk_path[0] + mode_path[0] + end_walk_path[0], start_walk_path[1][:-1]+ mode_path[1] + end_walk_path[1][1:]]


def choose_optimum_route(central_path, other_paths, case):

    def speed(central_path, other_paths):
        start_paths = []
        for path in other_paths['start']:
            start_paths.append(path['length'])
        min_start_path = start_paths.index(min(start_paths))
        end_paths = []
        for path in other_paths['end']:
            end_paths.append(path['length'])
        min_end_path = end_paths.index(min(end_paths))
        total_length = other_paths['start'][min_start_path]['length'] + central_path['length'] + other_paths['end'][min_end_path]['length']
        full_path = other_paths['start'][min_start_path]['route'][:-1] + central_path['route'] + other_paths['end'][min_end_path]['route'][1:]
        modes = [other_paths['start'][min_start_path]['mode'], central_path['mode'], other_paths['end'][min_end_path]['mode']]
        return {'length': total_length, 'modes': modes, 'route': full_path}

    def avoid_modes(central_path, other_paths, avoid):
        start_paths = []
        for path in other_paths['start']:
            if path['mode'] not in avoid:
                start_paths.append(path['length'])
        min_start_path = min(start_paths)
        for path in other_paths['start']:
            if path['length'] == min_start_path:
                min_start_path = path
                break
        end_paths = []
        for path in other_paths['end']:
            if path['mode'] not in avoid:
                #print(path)
                end_paths.append(path['length'])
        min_end_path = min(end_paths)
        for path in other_paths['end']:
            if path['length'] == min_end_path and path['mode'] not in avoid:
                min_end_path = path
                break
        total_length = min_start_path['length'] + central_path['length'] + min_end_path['length']
        full_path = min_start_path['route'][:-1] + central_path['route'] + min_end_path['route'][1:]
        modes = [min_start_path['mode'], central_path['mode'], min_end_path['mode']]
        return {'length': total_length, 'modes': modes, 'route': full_path}


    if case == 1:
        return speed(central_path, other_paths)
    if case == 2:
        avoid = ['bus', 'drive']
        path = avoid_modes(central_path, other_paths, avoid)
        if len(set(path['modes'])) < 3:
            avoid = ['drive']
            return avoid_modes(central_path, other_paths, avoid)
        else:
            return path
    if case == 3:
        avoid = ['']

def get_coordinates(start, end, case, graph_location):
    with open(graph_location, 'rb') as jfile:
        graph_json = geojson.load(jfile)
        G = nx.node_link_graph(graph_json)
    paths = dict()
    paths['bus'] = start_here(start, end, 'bus', ['luas', 'drive'], case, G)
    paths['luas'] = start_here(start, end, 'luas', ['bus', 'drive'], case, G)
    primary_path = None
    if paths['bus']['length'] < paths['luas']['length']:
        shortest = 'bus'
        if len(set(paths['bus']['modes'])) > 2:
            primary_path = paths['bus']
    else:
        shortest = 'luas'
        if len(set(paths['luas']['modes'])) > 2:
            primary_path = paths['luas']

    if not primary_path:
        print("Warning, no path with at least 2 modes of transport, returning shortest")
        primary_path = paths[shortest]

    coordinates = []
    for node in primary_path['route']:
        print (G.nodes.data()[node]['y'])
        coordinates.append(G.nodes.data()[node]['y'])
        coordinates.append(G.nodes.data()[node]['x'])

    return coordinates


# coordinates = []

# for node in primary_path['route']:
#    coordinates.append(G.nodes.data()[node]['y'])
#    coordinates.append(G.nodes.data()[node]['x'])
# print(coordinates)
# with open('coord.csv', "w") as file:
#     writer = csv.writer(file, delimiter=',')
#     writer.writerow(coordinates)
