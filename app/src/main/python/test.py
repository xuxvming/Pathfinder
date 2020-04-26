# -*- coding: utf-8 -*-
"""
Created on Wed Jan 29 16:18:01 2020

@author: Group 12
"""
from android_script import *
import android_script
import unittest
import json
import geojson
import networkx as nx

class TestAndroidScript(unittest.TestCase):
    maxDiff = None

    def setUp(self):
        with open('testing.json', 'r') as f:
            self.result = json.load(f)
        with open('testing_input.json', 'r') as f:
            self.input = json.load(f)
        self.start_coord = (53.2815126, -6.2341631)
        self.end_coord = (53.3881208173444, -6.2659470210)
        self.graph_location = '../assets/graph.json'
        with open(self.graph_location, 'rb') as jfile:
            graph_json = geojson.load(jfile)
            self.G = nx.node_link_graph(graph_json)

    def test_valid_edge_invalid_drive(self):
        self.assertFalse(valid_edge({"highway": "pedestrian"}, 'drive'))

    def test_valid_edge_valid_drive(self):
        self.assertTrue(valid_edge({"highway": "motorway"}, 'drive'))

    def test_valid_edge_valid_walk(self):
        self.assertTrue(valid_edge({"highway": "pedestrian"}, 'walk'))

    def test_valid_edge_invalid_walk(self):
        self.assertFalse(valid_edge({"highway": "motorway"}, 'walk'))

    def test_valid_edge_invalid_cycle(self):
        self.assertFalse(valid_edge({"highway": "pedestrian"}, 'cycle'))

    def test_valid_edge_valid_cycle(self):
        self.assertTrue(valid_edge({"highway": "cycleway"}, 'cycle'))

    def test_valid_edge_valid_luas(self):
        self.assertTrue(valid_edge({"luas":"Red"}, 'luas'))

    def test_valid_edge_invalid_luas(self):
        self.assertFalse(valid_edge({"highway": "motorway"}, 'luas'))

    def test_valid_edge_invalid_bus(self):
        self.assertFalse(valid_edge({"highway": "pedestrian"}, 'bus'))

    def test_valid_edge_valid_bus(self):
        self.assertTrue(valid_edge({"bus": "lane"}, 'bus'))

    def test_valid_edge_valid_dart(self):
        self.assertTrue(valid_edge({"dart": "12"}, 'dart'))

    def test_valid_edge_invalid_dart(self):
        self.assertFalse(valid_edge({"bus": "12"}, 'dart'))

    def test_dijkstra_if_source_not_in_graph(self):
        with self.assertRaises(nx.NodeNotFound):
            dijkstra(self.G, sources={-999}, weight='length', pred=None, paths=None, cutoff=None, target=12435, mode='walk')

    def test_check_if_mode_possible_from_node(self):
        self.assertTrue(check_if_mode_possible_from_node(self.G, 10, 'drive'))

    def test_check_if_mode_not_possible_from_node(self):
        self.assertFalse(check_if_mode_possible_from_node(self.G, 10, 'bus'))

    def test_shortest_path_no_path_to_target(self):
        with self.assertRaises(nx.NetworkXNoPath):
            shortest_path(self.G, sources=1, target=-999, cutoff=None, weight='length', mode='drive')

    def test_shortest_path_sources_none(self):
        with self.assertRaises(ValueError):
            shortest_path(self.G, sources=None, target=None, cutoff=None, weight='length', mode='drive')

    def test_distance(self):
        self.assertEqual(distance(self.start_coord[0], self.start_coord[1], self.end_coord[0], self.end_coord[1]), 12040.68462774101)

    def test_closest_n_nodes_to_co_ordinate_returns_number_of_nodes(self):
        self.assertEqual(len(closest_n_nodes_to_co_ordinate(self.G, self.start_coord, 6, 'drive')), 6)

    def test_closest_node_to_co_ordinate(self):
        self.assertEqual(android_script.closest_node_to_co_ordinate(self.G, self.start_coord, 'walk'), (60936880, 25.190513509555398))

    def test_get_coordinates(self):
        self.assertDictEqual(android_script.get_coordinates(self.start_coord,self.end_coord, 0, self.graph_location), self.result['get_coordinates'])

    def test_start_here(self):
        self.assertDictEqual(android_script.start_here(self.start_coord, self.end_coord, 'bus', ['luas', 'drive'], 0, self.G), self.result['start_here'])

    def test_get_best_bus_path(self):
        self.assertDictEqual(android_script.get_best_bus_path(self.start_coord, 1115, self.G), self.result['get_best_bus_path'])

    def test_get_central_path(self):
        self.assertDictEqual(android_script.get_central_path(self.start_coord, self.end_coord, 'bus', self.G), self.result['get_central_path'])

    def test_fill_rest_of_journey(self):
        self.assertDictEqual(android_script.fill_rest_of_journey(self.start_coord, (53.31096389, -6.199823889), 'luas', self.G), self.result['fill_rest_of_journey'])

    def test_choose_optimum_route(self):
        self.assertDictEqual(android_script.choose_optimum_route(self.input['choose_optimum_route_central_path'], self.input['choose_optimum_route_paths'], 0), self.result['choose_optimum_route'])

    def test_format_modes(self):
        self.assertEqual(android_script.format_modes(self.input['format_modes_other_paths_start'], self.input['format_modes_central_path'], self.input['format_modes_other_paths_end']), self.result['format_modes'])

    def test_full_path(self):
        self.assertEqual(android_script.format_full_path(self.input['format_full_path_other_paths_start'], self.input['format_full_path_central_path'], self.input['format_full_path_other_paths_end']), self.result['format_full_path'])

    def test_check_if_its_short_path(self):
        isShort, short_path = android_script.check_if_its_short_path(60936880, 5337558071, self.G)
        self.assertEqual([isShort, short_path], self.result['check_if_its_short_path'])

    def test_get_paths(self):
        self.assertDictEqual(android_script.get_paths(self.start_coord, self.end_coord, 0, self.G), self.result['get_paths'])

    def test_calculate_coordinates(self):
        self.assertDictEqual(android_script.calculate_coordinates(self.G, self.input['calculate_coordinates_paths']), self.result['calculate_coordinates'])

if __name__ == '__main__':
    unittest.main()
