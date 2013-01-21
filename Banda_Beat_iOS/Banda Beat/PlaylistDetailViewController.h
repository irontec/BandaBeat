//
//  PlaylistPlayerViewController.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import <CoreData/CoreData.h>
#import "GAITrackedViewController.h"


@interface PlaylistDetailViewController : GAITrackedViewController
@property(weak,nonatomic) NSManagedObjectContext *context;
@property(weak,nonatomic) IBOutlet UITableView *tableView;
@property(strong,nonatomic) NSArray *tracks;
@property(strong,nonatomic) NSNumber *idPlaylist;
@property(strong,nonatomic) NSString *playlistTitle;
-(IBAction)setFavorite:(id)sender;
@end
